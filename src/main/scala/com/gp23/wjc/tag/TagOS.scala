package com.gp23.wjc.tag

import org.apache.spark.sql.Row

object TagOS extends Tag {
  override def makeTags(args: Any*): List[(String, Int)] = {
    var list = List[(String,Int)]()
    val row = args(0).asInstanceOf[Row]
    val id = row.getAs[Int]("client")
    id match {
      case 1 => list:+=("Android D00010001",1)
      case 2 => list:+=("IOS D00010002",1)
      case 3 => list:+=("WinPhone D00010003",1)
      case _ => list:+=("其 他 D00010004",1)
    }
     list
  }
}
