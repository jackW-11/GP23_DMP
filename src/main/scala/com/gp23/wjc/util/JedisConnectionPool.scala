package com.gp23.wjc.util

import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}

object JedisConnectionPool {
  //连接redis
  //连接池配置文件
  val config = new JedisPoolConfig()
  config.setMaxTotal(20)
  config.setMaxIdle(10)
  //jedis对象
  private val pool =
    new JedisPool(config,"hadoop102",6379,10000,"123456")
  def getConnection(): Jedis ={
    pool.getResource
  }
}
