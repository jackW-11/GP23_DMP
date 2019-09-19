package com.gp23.wjc.point

import com.gp23.wjc.util.{PointUtil, Sink2Mysql}
import org.apache.spark.sql.{SaveMode, SparkSession}

object GroupbyLocation {
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
    /**需求一：省份城市数目汇总，输出到mysql和磁盘*/
    import org.apache.spark.sql.functions._
    val groupbylocation = df.groupBy("provincename", "cityname")
      .agg(count("*") as "counts")
    groupbylocation.write.mode(SaveMode.Overwrite)
      .json("E:\\gp23_Data\\groupbylocation")
    //工具类的方式写入到mysql
    Sink2Mysql.tomySQL(groupbylocation,"groupbylocation")
    spark.close()
  }
}
