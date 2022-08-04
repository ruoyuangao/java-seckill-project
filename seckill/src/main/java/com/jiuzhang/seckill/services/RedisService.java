package com.jiuzhang.seckill.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.Collections;

@Slf4j
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

    /**
     * Check whether user is in limit member list
     * @param seckillActivityId
     * @param userId
     * @return
     */
    public boolean isInLimitMember(long seckillActivityId, long userId) {
        Jedis jedisClient = jedisPool.getResource();
        boolean sismember = jedisClient.sismember("seckillActivity_users:" + seckillActivityId, String.valueOf(userId));
        log.info("userId:{} activityId:{} is in the limit member list: {}", userId, seckillActivityId, sismember);
        return sismember;
    }

    /**
     * Add user to limit member list
     *
     * @param seckillActivityId
     * @param userId
     */
    public void addLimitMember(long seckillActivityId, long userId) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.sadd("seckillActivity_users:" + seckillActivityId, String.valueOf(userId));
        jedisClient.close();
    }

    /**
     * Remove user from limit member list
     *
     * @param seckillActivityId
     * @param userId
     */
    public void removeLimitMember(Long seckillActivityId, Long userId) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.srem("seckillActivity_users:" + seckillActivityId, String.valueOf(userId));
        jedisClient.close();
    }
}





