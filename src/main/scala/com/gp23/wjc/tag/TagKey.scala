package com.gp23.wjc.tag

import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.Row

object TagKey extends Tag {
  override def makeTags(args: Any*): List[(String, Int)] = {
    var list = List[(String,Int)]()
    val row = args(0).asInstanceOf[Row]
    val keys = row.getAs[String]("keywords")
    if (StringUtils.isNotBlank(keys)){
      //（标签格式：Kxxx->1）xxx 为关键字，关键字个数不能少于 3 个字符，且不能
      //超过 8 个字符；关键字中如包含‘‘|’’，则分割成数组，转化成多个关键字标签
      if(keys.contains("|")){
        val keysarr = keys.split("\\|")
        for (item<-keysarr){
          if (item.length>=1 && item.length<=8){
            list:+=("K:"+item,1)
          }
        }
      }else{
        if (keys.length>=1 && keys.length<=8){
          list:+=("K:"+keys,1)
        }
      }
    }
    list
  }
}
