package com.dezheng.listen;


import com.alibaba.fastjson.JSON;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import java.util.Map;

public class OrderStatusListen implements MessageListener {
    @Override
    public void onMessage(Message message) {
        String string = new String(message.getBody());
        Map resultMap = JSON.parseObject(string, Map.class);
        String orderId = (String) resultMap.get("orderId");
    }
}
