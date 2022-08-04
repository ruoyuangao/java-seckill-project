package com.jiuzhang.seckill.services;

import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.Collections;

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

    /** * 缓存中库存判断和扣减 * @param key * @return * @throws Exception */
    public boolean stockDeductValidation(String key) {
        try(Jedis client = jedisPool.getResource()) {
            String script = "if redis.call('exists',KEYS[1]) == 1 then\n" +
                    "    local stock = tonumber(redis.call('get', KEYS[1]))\n" +
                    "    if( stock <=0 ) then\n" +
                    "        return -1\n" +
                    "    end;\n" +
                    "    redis.call('decr',KEYS[1]);\n" +
                    "    return stock -1;\n" +
                    "end;\n" +
                    "return -1;";
            Long stock = (Long) client.eval(script, Collections.singletonList(key), Collections.emptyList());

            if (stock < 0) {
                System.out.println("Stock not enough");
                return false;
            }
            System.out.println("Congratulations! you have successully seckill the item.");
            return true;
        } catch (Throwable throwable) {
            System.out.println("the error is:" + throwable.toString());
            return false;
        }
    }

    /**
     * timeout order roll back
     * @param key
     */
    public void revertStock(String key) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.incr(key);
        jedisClient.close();
    }
}





