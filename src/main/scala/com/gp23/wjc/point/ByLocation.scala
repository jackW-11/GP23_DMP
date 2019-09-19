package com.gp23.wjc.point

import com.gp23.wjc.util.{PointUtil, Sink2Mysql}
import org.apache.spark.sql.{SaveMode, SparkSession}

object ByLocation {
  def main(args: Array[String]): Unit = {
    //path
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
    /**需求二：在各个维度下，输出到mysql和磁盘*/
    //1.地域分布
    val location = df.select(
      "provincename",
      "cityname",
      "requestmode",
      "processnode",
      "iseffective",
      "isbilling",
      "isbid",
      "iswin",
      "adorderid",
      "winprice",
      "adpayment"
    )
    Sink2Mysql.tomySQL(location,"location")
    //sql方式
    location.createOrReplaceTempView("location")
    val sql1 = "select\nprovincename,\ncityname,\nsum(case when requestmode=1 then 1 else 0 end) firstnum,\nsum(case when requestmode=1 and processnode>=2 then 1 else 0 end) isnum,\nsum(case when requestmode=1 and processnode=3 then 1 else 0 end) truenum,\nsum(case when requestmode=2 and iseffective=1 then 1 else 0 end) shownum,\nsum(case when requestmode=3 and iseffective=1 then 1 else 0 end) clicknum,\nsum(case when iseffective=1 and isbilling=1 and isbid=1 then 1 else 0 end) bidnum,\nsum(case when iseffective=1 and isbilling=1 and isbid=1 and iswin=1 and adpayment!=0 then 1 else 0 end) winnum,\nsum(case when iseffective=1 and isbilling=1 and iswin=1 then winprice/1000.0 else 0 end) winprice,\nsum(case when iseffective=1 and isbilling=1 and iswin=1 then adpayment/1000.0 else 0 end) adpay\nfrom location\ngroup by provincename,cityname"
    val res1 = spark.sql(sql1)
    res1.write.partitionBy("provincename","cityname")
      .mode(SaveMode.Overwrite)
      .json("E:\\gp23_Data\\point\\bylocation")
    Sink2Mysql.tomySQL(res1,"bylocation")
    //core方式
    location.rdd.map(row=>{
      val provincename = row.getAs[String]("provincename")
      val cityname = row.getAs[String]("cityname")
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
      ((provincename,cityname),allList)
    }).reduceByKey((list1,list2)=>list1.zip(list2).map(t=>t._1+t._2))
      .map(x=>(x._1._1,x._1._2,x._2))
      .toDF("province","city","value").show()
    spark.stop()
  }

}
