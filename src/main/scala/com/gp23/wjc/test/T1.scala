package com.gp23.wjc.test

object T1 {
  def main(args: Array[String]): Unit = {
    var list = List[(String,Int)]()
    list:+=("a",1)
    list:+=("b",1)
    list.foreach(print)
  }
}
