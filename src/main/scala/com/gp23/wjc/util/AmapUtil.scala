package com.gp23.wjc.util

import com.alibaba.fastjson.{JSON,JSONObject}
import scala.collection.mutable.{ArrayBuffer, ListBuffer}
/**
  * 解析经纬度
  * */
object AmapUtil {
  def getBussinessFromLocation(long:Double,lat:Double):String={
    val location = long+","+lat
    //"https://restapi.amap.com/v3/geocode/regeo?&location=116.310003,39.991957&key=8252cb95c8de29c5ff8be39778708a93&radius=1000&extensions=all")
    val url = "https://restapi.amap.com/v3/geocode/regeo?&location="+location+"&key=8252cb95c8de29c5ff8be39778708a93"
  //调用接口
    val str = HTTPUtil.get(url)
    //创建json对象
    val jSONObject1 = JSON.parseObject(str)
    //解析json
    val statu = jSONObject1.getIntValue("status")
    if (statu!=1) return ""
    val jSONObject2 = jSONObject1.getJSONObject("regeocode")
    if(jSONObject2 == null) return ""
    val jSONObject3 = jSONObject2.getJSONObject("addressComponent")
    if (jSONObject3 == null) return ""
    val bus = jSONObject3.getJSONArray("businessAreas")
    if (bus == null) return ""
    val buffer = ListBuffer[String]()
    for(item <- bus.toArray()){
      if (item.isInstanceOf[JSONObject]) {
        val json = item.asInstanceOf[JSONObject]
        val name = json.getString("name")
        buffer.append(name)
      }
    }
    //得到的商圈名字
    buffer.mkString(",")
  }
}
