package com.gp23.wjc.util

object String2Type {
  def toInt(i:String):Int={
    try{
      i.toInt
    }catch{
      case _ => 0
    }
  }
  def toDouble(d:String):Double={
    try{
      d.toDouble
    }catch{
      case _ => 0.0
    }
  }

}
