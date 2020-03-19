package com.dezheng.listen;


import com.alibaba.fastjson.JSON;
import com.dezheng.service.order.OrderService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

public class OrderStatusListen implements MessageListener {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void onMessage(Message message) {
        String string = new String(message.getBody());
        Map resultMap = JSON.parseObject(string, Map.class);
        String orderId = (String) resultMap.get("orderId");
        String transactionId = (String) resultMap.get("transactionId");
        System.out.println(orderId);
        System.out.println(transactionId);

        String s = orderService.updateOrderStatus(orderId, transactionId);
        System.out.println(s);
    }
}
