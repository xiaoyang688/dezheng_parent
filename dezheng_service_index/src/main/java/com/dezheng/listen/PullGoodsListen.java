package com.dezheng.listen;

import com.alibaba.fastjson.JSON;
import com.dezheng.service.IndexService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;

public class PullGoodsListen implements ChannelAwareMessageListener {

    @Autowired
    private IndexService indexService;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        String msg = new String(message.getBody());
        System.out.println(msg);
        String[] spuIdList = JSON.parseObject(msg, String[].class);

        String status = indexService.delIndex(spuIdList);
        System.out.println(status);
    }
}
