package com.gp23.wjc.test

import com.gp23.wjc.util.HTTPUtil
import org.apache.spark.sql.SparkSession

object HttpTest {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName(this.getClass.getName)
      .master("local[*]")
      .getOrCreate()
    val arr = Array("https://restapi.amap.com/v3/geocode/regeo?&location=116.310003,39.991957&key=8252cb95c8de29c5ff8be39778708a93&radius=1000&extensions=all")
    val rdd = spark.sparkContext.makeRDD(arr)
    rdd.map(t=>{
      HTTPUtil.get(t)
    }).foreach(println)
  }

}
