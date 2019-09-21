package com.gp23.wjc.tag

import com.gp23.wjc.util.TagUtil
import org.apache.spark.sql.SparkSession

/**
  * 上下文标签的主类
  * */
object TagContext {
  def main(args: Array[String]): Unit = {
    //创建spark
    val spark = SparkSession.builder()
      .appName(this.getClass.getName)
      .master("local[*]")
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .getOrCreate()
    import spark.implicits._
    val df = spark.read.parquet("E:\\gp23_Data\\etl")
    val docmap1 = spark.sparkContext.textFile("E:\\gp23_Data\\source\\app_dict.txt")
      .map(_.split("\\s", -1))
      .filter(_.length >= 5)
      .map(arr => (arr(4), arr(1)))
      .collectAsMap()
    val bromap1 = spark.sparkContext.broadcast(docmap1)
    //处理数据信息
    df.map(row=>{
      //获取用户id
      val userId = TagUtil.getUserId(row)
      //打标签
      val adTags = TagAd.makeTags(row)
      //app名称
      //先得到所有的appnam
      val appid = row.getAs[String]("appid")
      val appname = bromap1.value.getOrElse(appid,"未知")
      val appnameTags = ("App:"+appname,1)
      //3)渠道（标签格式： CNxxxx->1）xxxx 为渠道 ID(adplatformproviderid)
      val adplatformproviderid = TagCN.makeTags(row)
      //设备类型
      val os = TagOS.makeTags(row)
      //运营商
      val isp = TagIsp.makeTags(row)
      //设备联网方式
      val net = TagNet.makeTags(row)
      //5)关键字
      val keys = TagKey.makeTags(row)
      //地域标签（省标签格式：ZPxxx->1, 地市标签格式: ZCxxx->1）xxx 为省或市名称
      val location = TagLocation.makeTags(row)
      (userId,adTags,appnameTags,adplatformproviderid,os,isp,net,keys,location)
      //商圈
//      val busTags = TagBusiness.makeTags(row)
//      (userId,adTags,appnameTags,adplatformproviderid,os,isp,net,keys,location,busTags)
    }).rdd.foreach(println)
  }

}
