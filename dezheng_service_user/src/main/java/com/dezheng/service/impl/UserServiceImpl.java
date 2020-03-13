package com.dezheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.dezheng.dao.UserMapper;
import com.dezheng.pojo.user.User;
import com.dezheng.service.user.UserService;
import com.dezheng.utils.BCrypt;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

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
        redisTemplate.boundValueOps(phone).set(code + "");
        redisTemplate.boundValueOps("phone").expire(5, TimeUnit.MINUTES);

        //发送mq信息,使用直接模式
        Map codeMap = new HashMap();
        codeMap.put("phone", phone);
        codeMap.put("code", code + "");
        rabbitTemplate.convertAndSend("", "queue.sms", JSON.toJSONString(codeMap));
    }

    @Override
    public void register(User user, String code) {
        //校验
        if (user.getUsername() == null) {
            throw new RuntimeException("请输入正确手机号码");
        }
        if (user.getPassword().length() < 5) {
            throw new RuntimeException("密码少于六位");
        }
        if ("".equals(code) || code == null) {
            throw new RuntimeException("验证码为空");
        }
        //查询是否有存在用户名
        User searchUser = userMapper.selectByPrimaryKey(user.getUsername());

        if (searchUser != null) {//用户已存在
            throw new RuntimeException("用户已存在");
        } else {//用户不存在

            //获取系统验证码
            String sysCode = (String) redisTemplate.boundValueOps(user.getUsername()).get();

            //获取验证码为空
            if (sysCode == null) {
                throw new RuntimeException("验证码失效了");
            }

            System.out.println(sysCode);
            System.out.println(code);

            //校验验证码
            if (sysCode.equals(code)) {
                //验证成功
                user.setCreateTime(new Date());
                user.setUpdateTime(new Date());
                user.setPhone(user.getUsername());
                user.setIsEmailCheck("0");
                user.setStatus("1");
                userMapper.insertSelective(user);
            } else {
                throw new RuntimeException("验证码错误");
            }
        }
    }

    @Override
    public boolean checkUser(User user) {

        User searchUser = userMapper.selectByPrimaryKey(user.getUsername());
        if (searchUser == null) {
            throw new RuntimeException("用户未注册");
        }
        //检验密码
        if (BCrypt.checkpw(user.getPassword(), searchUser.getPassword())) {
            return true;
        } else {
            throw new RuntimeException("密码错误");
        }
    }

    @Override
    public Map getUserInfo(String username) {

        //生成token
        String token = UUID.randomUUID().toString();

        //保存token对应的username
        try {
            redisTemplate.boundValueOps(token).set(username);
        } catch (Exception e) {
            throw new RuntimeException("用户名存入redis失败");
        }

        //查询用户信息
        User user = userMapper.selectByPrimaryKey(username);

        //封装用户信息
        Map result = new HashMap();

        result.put("username", user.getUsername());
        result.put("haedPic", user.getHeadPic());
        result.put("token", token);

        return result;

    }

}
