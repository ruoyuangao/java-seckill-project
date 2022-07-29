package com.jiuzhang.seckill.services;

import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;

@Service
public class RedisService {

    @Resource
    private JedisPool jedisPool;

    public RedisService setValue(String key, Long value) {
        Jedis client = jedisPool.getResource();
        client.set(key, value.toString());
        client.close();
        return this;
        //链式调用
    }

    public String getValue(String key) {
        Jedis client = jedisPool.getResource();
        String value = client.get(key);
        client.close();
        return value;
    }
}
