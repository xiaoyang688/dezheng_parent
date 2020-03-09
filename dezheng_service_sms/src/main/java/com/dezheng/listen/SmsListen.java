package com.dezheng.listen;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.CommonResponse;
import com.dezheng.aliyun.AliyunSms;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class SmsListen implements MessageListener {

    @Autowired
    private AliyunSms aliyunSms;

    public void onMessage(Message message) {
        String messageString = new String(message.getBody());
        Map messageMap = JSON.parseObject(messageString, Map.class);
        String phone = (String) messageMap.get("phone");
        String code = (String) messageMap.get("code");

        System.out.println(phone + ":" + code);
        //发送短信
        CommonResponse response = aliyunSms.sendSms(phone, code);
        System.out.println(response.getData());
    }
}
