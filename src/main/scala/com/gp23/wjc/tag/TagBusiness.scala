package com.gp23.wjc.tag

import ch.hsr.geohash.GeoHash
import com.gp23.wjc.util.{AmapUtil, JedisConnectionPool, String2Type}
import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.Row

object TagBusiness extends Tag {
  override def makeTags(args: Any*): List[(String, Int)] = {
    var list = List[(String, Int)]()
    val row = args(0).asInstanceOf[Row]
    //判断经纬度范围，只解析中国商圈
    if (String2Type.toDouble(row.getAs[String]("long")) >= 73
      && String2Type.toDouble(row.getAs[String]("long")) <= 136
      && String2Type.toDouble(row.getAs[String]("lat")) >= 3
      && String2Type.toDouble(row.getAs[String]("lat")) <= 53) {
      val long = row.getAs[String]("long").toDouble
      val lat = row.getAs[String]("lat").toDouble
      val str = getBusiness(long, lat)
      if (StringUtils.isNoneBlank(str)) {
        val strings = str.split(",")
        strings.foreach(bu => {
          list :+= (bu, 1)
        })
      }
    }
    list
  }
    //调用geohash维护商圈
    def getBusiness(long:Double,lat:Double): String ={
      //维护geohash
      val geoHash = GeoHash.geoHashStringWithCharacterPrecision(lat,long,6)
      //检查redis是否含有
      var bus = redis_request(geoHash)
      if (bus==null){
        //从高德请求
        bus = AmapUtil.getBussinessFromLocation(long,lat)
        if (bus!=null&&bus.length>0){
          //存入redis
          redis_insertBusiness(geoHash,bus)
        }
      }
      bus
    }
    //redis数据库取
    def redis_request(geoHash: String):String = {
      val jedis = JedisConnectionPool.getConnection()
      val business = jedis.get(geoHash)
      jedis.close()
      business
    }
    //redis数据库存
    def redis_insertBusiness(geoHash: String, str: String) = {
      val jedis = JedisConnectionPool.getConnection()
      jedis.set(geoHash,str)
      jedis.close()
    }

}
