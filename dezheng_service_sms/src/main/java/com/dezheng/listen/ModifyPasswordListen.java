package com.dezheng.listen;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.CommonResponse;
import com.dezheng.aliyun.AliyunSms;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class ModifyPasswordListen implements MessageListener {

    @Autowired
    private AliyunSms aliyunSms;

    @Override
    public void onMessage(Message message) {
        Map messageMap = JSON.parseObject(new String(message.getBody()), Map.class);
        String phone = (String) messageMap.get("phone");
        String code = (String) messageMap.get("code");

        System.out.println(phone + ":" + code);
        //发送短信
        CommonResponse response = aliyunSms.sendSms(phone, code, "SMS_186610959");
        System.out.println(response.getData());
    }
}
