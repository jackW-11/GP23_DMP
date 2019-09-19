package com.gp23.wjc.point

import com.gp23.wjc.util.PointUtil
import org.apache.spark.sql.{SaveMode, SparkSession}

object ByType {
  def main(args: Array[String]): Unit = {
    //媒体分析根据数据字典来确定媒体类型，运用广播变量
    val spark = SparkSession.builder()
      .appName(this.getClass.getName)
      .master("local[*]")
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .getOrCreate()
    import spark.implicits._
    //读取数据字典文件并处理
    val docrdd = spark.sparkContext.textFile("E:\\gp23_Data\\source\\app_dict.txt")
    val docmap = docrdd.map(_.split("\\s", -1))
      .filter(_.length >= 5)
      .map(arr => {
        (arr(0), arr(1))
      }).collectAsMap()
    //将处理后的数据字典广播出去
    val doc = spark.sparkContext.broadcast(docmap)
    val df = spark.read.parquet("E:\\gp23_Data\\etl")
    val dfapp = df.select(
      "adplatformproviderid",
      "requestmode",
      "processnode",
      "iseffective",
      "isbilling",
      "isbid",
      "iswin",
      "adorderid",
      "winprice",
      "adpayment")
    dfapp.show(1000)
    dfapp.rdd.map(row=>{
      val requestmode = row.getAs[Int]("requestmode")
      val processnode = row.getAs[Int]("processnode")
      val iseffective = row.getAs[Int]("iseffective")
      val isbilling = row.getAs[Int]("isbilling")
      val isbid = row.getAs[Int]("isbid")
      val iswin = row.getAs[Int]("iswin")
      val adorderid = row.getAs[Int]("adorderid")
      val winprice = row.getAs[Double]("winprice")
      val adpayment = row.getAs[Double]("adpayment")
      //根据计算逻辑对每个数据进行处理
      val list1 = PointUtil.getrequest(requestmode,processnode)
      val list2 = PointUtil.shownum(requestmode,iseffective)
      val list3 = PointUtil.adnum(iseffective,isbilling,isbid,iswin,adorderid,winprice,adpayment)
      val allList = list1 ++ list2 ++ list3
      //通过数据字典对key进行护理
      val adplatformproviderid = row.getAs[Int]("adplatformproviderid")
      val adplatformprovider = doc.value.getOrElse(adplatformproviderid.toString,"unknow")
      (adplatformprovider,allList)
    }).reduceByKey((list1,list2)=>list1.zip(list2).map(t=>t._1+t._2))
      .toDF("name","value")
      .write.mode(SaveMode.Overwrite)
      .partitionBy("name")
      .json("E:\\gp23_Data\\point\\bytype")
  }
}
