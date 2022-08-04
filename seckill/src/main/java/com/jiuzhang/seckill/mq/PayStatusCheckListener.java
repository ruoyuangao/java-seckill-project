package com.jiuzhang.seckill.mq;

import com.alibaba.fastjson.JSON;
import com.jiuzhang.seckill.db.dao.OrderDao;
import com.jiuzhang.seckill.db.dao.SeckillActivityDao;
import com.jiuzhang.seckill.db.po.Order;
import com.jiuzhang.seckill.services.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RocketMQMessageListener(topic = "pay_check", consumerGroup = "pay_check_group")
public class PayStatusCheckListener implements RocketMQListener<MessageExt> {

    @Resource
    private OrderDao orderDao;

    @Resource
    private SeckillActivityDao seckillActivityDao;

    @Resource
    private RedisService redisService;

    @Override
    @Transactional
    public void onMessage(MessageExt messageExt) {
        String message = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        log.info("Receive the payment status check message: " + message);
        Order order = JSON.parseObject(message, Order.class);
        //1. query for the order
        Order orderInfo = orderDao.queryOrder(order.getOrderNo());
        //2. Check whether the order has been paid
        if (orderInfo.getOrderStatus() != 2) {
            //3. Not been paid, so close the order
            log.info("The order has not been paid in time, order ID: " + orderInfo.getOrderNo());
            orderInfo.setOrderStatus(99);
            orderDao.updateOrder(orderInfo);
            //4. revert the stock in database
            seckillActivityDao.revertStock(order.getSeckillActivityId());
            //revert the stock in redis
            redisService.revertStock("stock:" + order.getSeckillActivityId());
            //remove timeout user from limit member list
            redisService.removeLimitMember(order.getSeckillActivityId(), order.getUserId());

        }

    }

}


