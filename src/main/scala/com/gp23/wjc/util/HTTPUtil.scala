package com.gp23.wjc.util

import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

object HTTPUtil {
  //get请求
  def get(url:String):String={
    //创建一个客户端
    val clients = HttpClients.createDefault()
    val httpGet = new HttpGet(url)
    val response = clients.execute(httpGet)//发送
    //处理结果
    EntityUtils.toString(response.getEntity,"UTF-8")

  }

}
