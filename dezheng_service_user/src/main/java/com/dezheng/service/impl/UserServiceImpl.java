package com.dezheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.dezheng.service.user.UserService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendSms(String phone) {

        //校验
        if (phone.length() < 10) {
            throw new RuntimeException("手机号码不正确！");
        }

        Random random = new Random();
        int code = random.nextInt(9999);
        System.out.println(code);
        //生成少于四位
        if (code <= 999) {
            code += 1000;
        }
        //将验证码存入redis
        redisTemplate.boundValueOps(phone).set(code);

        //发送mq信息,使用直接模式
        Map codeMap = new HashMap();
        codeMap.put("phone", phone);
        codeMap.put("code", code+"");
        rabbitTemplate.convertAndSend("", "queue.sms", JSON.toJSONString(codeMap));
    }

}
