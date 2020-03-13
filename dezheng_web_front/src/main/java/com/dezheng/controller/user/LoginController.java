package com.dezheng.controller.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dezheng.entity.Result;
import com.dezheng.pojo.user.User;
import com.dezheng.service.user.UserService;
import com.dezheng.utils.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Reference
    private UserService userService;

    @GetMapping("/sendSms")
    public Result sendSms(@RequestParam("phone") String phone) {
        userService.sendSms(phone);
        return new Result(1, "发送验证码成功！");
    }

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        String password = user.getPassword();
        String gensalt = BCrypt.gensalt();
        String hashpw = BCrypt.hashpw(password, gensalt);
        user.setPassword(hashpw);
        userService.register(user, user.getCode());
        return new Result(1, "注册成功！");
    }

    @PostMapping("/signIn")
    private Map signIn(@RequestBody User user) {

        Map map = new HashMap();
        //检验用户
        if (userService.checkUser(user)) { //校验成功
            return userService.getUserInfo(user.getUsername());
        } else {
            return map;
        }
    }
}
