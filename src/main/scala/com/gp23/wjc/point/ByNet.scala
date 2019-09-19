package com.gp23.wjc.point

import com.gp23.wjc.util.{PointUtil, Sink2Mysql}
import org.apache.spark.sql.{SaveMode, SparkSession}

object ByNet {
  def main(args: Array[String]): Unit = {
    val inputpath = "E:\\gp23_Data\\etl"
    //创建对象
    val spark = SparkSession.builder()
      .appName(this.getClass.getName)
      .master("local[*]")
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .getOrCreate()
    //得到原始数据并缓存
    val df = spark.read.parquet(inputpath).cache()
    import spark.implicits._
    //终端设备
    val isp = df.select(
      "networkmannername",
      "requestmode",
      "processnode",
      "iseffective",
      "isbilling",
      "isbid",
      "iswin",
      "adorderid",
      "winprice",
      "adpayment")
    Sink2Mysql.tomySQL(isp,"network")
    isp.createOrReplaceTempView("network")
    val sql3 = "select\nnetworkmannername,\nsum(case when requestmode=1 then 1 else 0 end) firstnum,\nsum(case when requestmode=1 and processnode>=2 then 1 else 0 end) isnum,\nsum(case when requestmode=1 and processnode=3 then 1 else 0 end) truenum,\nsum(case when requestmode=2 and iseffective=1 then 1 else 0 end) shownum,\nsum(case when requestmode=3 and iseffective=1 then 1 else 0 end) clicknum,\nsum(case when iseffective=1 and isbilling=1 and isbid=1 then 1 else 0 end) bidnum,\nsum(case when iseffective=1 and isbilling=1 and isbid=1 and iswin=1 and adpayment!=0 then 1 else 0 end) winnum,\nsum(case when iseffective=1 and isbilling=1 and iswin=1 then winprice/1000.0 else 0 end) winprice,\nsum(case when iseffective=1 and isbilling=1 and iswin=1 then adpayment/1000.0 else 0 end) adpay\nfrom network\ngroup by networkmannername"
    val res3 = spark.sql(sql3)
    res3.write.partitionBy("networkmannername")
      .mode(SaveMode.Overwrite)
      .json("E:\\gp23_Data\\point\\bynet")
    Sink2Mysql.tomySQL(res3,"bynet")
    //core方式
    isp.rdd.map(row=>{
      val networkmannername = row.getAs[String]("networkmannername")
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
      (networkmannername,allList)
    }).reduceByKey((list1,list2)=>list1.zip(list2).map(t=>t._1+t._2))
      .map(x=>(x._1,x._2))
      .toDF("networkmannername","value").show()
    spark.stop()
  }
}
