package com.gp23.wjc.tag

import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.Row

object TagAd extends Tag {
  override def makeTags(args: Any*): List[(String, Int)] = {
    var list = List[(String,Int)]()
    //获取数据类型
    val row = args(0).asInstanceOf[Row]
    //获取广告位类型标签
    val adType = row.getAs[Int]("adspacetype")
    //广告位类型标签
    adType match {
      case v if v > 9 => list:+=("LC:"+v,1)
      case v if v > 0 && v<=9 => list:+=("LC:0"+v,1)
    }
    val adName = row.getAs[String]("adspacetypename")
    if (StringUtils.isNotBlank(adName)){
      list:+=("LN:"+adName,1)
    }
    list
  }
}
