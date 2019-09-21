package com.gp23.wjc.util

import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.Row

object TagUtil{
  //获取用户id的方法
  def getUserId(row:Row):String ={
    row match {
      case v if StringUtils.isNotBlank(v.getAs[String]("imei")) => "IM:"+v.getAs[String]("imei")
      case v if StringUtils.isNotBlank(v.getAs[String]("mac")) => "MC:"+v.getAs[String]("mac")
      case v if StringUtils.isNotBlank(v.getAs[String]("idfa")) => "ID:"+v.getAs[String]("idfa")
      case v if StringUtils.isNotBlank(v.getAs[String]("openudid")) => "OP:"+v.getAs[String]("openudid")
      case v if StringUtils.isNotBlank(v.getAs[String]("androidid")) => "AN:"+v.getAs[String]("androidid")
      case v if StringUtils.isNotBlank(v.getAs[String]("imeimd5")) => "IM:"+v.getAs[String]("imeimd5")
      case v if StringUtils.isNotBlank(v.getAs[String]("macmd5")) => "MC:"+v.getAs[String]("macmd5")
      case v if StringUtils.isNotBlank(v.getAs[String]("idfamd5")) => "ID:"+v.getAs[String]("idfamd5")
      case v if StringUtils.isNotBlank(v.getAs[String]("openudidmd5")) => "OP:"+v.getAs[String]("openudidmd5")
      case v if StringUtils.isNotBlank(v.getAs[String]("androididmd5")) => "AN:"+v.getAs[String]("androididmd5")
      case v if StringUtils.isNotBlank(v.getAs[String]("imeisha1")) => "IM:"+v.getAs[String]("imeisha1")
      case v if StringUtils.isNotBlank(v.getAs[String]("macsha1")) => "MC:"+v.getAs[String]("macsha1")
      case v if StringUtils.isNotBlank(v.getAs[String]("idfasha1")) => "ID:"+v.getAs[String]("idfasha1")
      case v if StringUtils.isNotBlank(v.getAs[String]("openudidsha1")) => "OP:"+v.getAs[String]("openudidsha1")
      case v if StringUtils.isNotBlank(v.getAs[String]("androididsha1")) => "AN:"+v.getAs[String]("androididsha1")
      case _ => "其他"
    }
  }
}
