package com.dezheng.controller.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dezheng.entity.Result;
import com.dezheng.pojo.user.User;
import com.dezheng.service.user.UserService;
import com.dezheng.utils.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {


    @Reference
    private UserService userService;

    @GetMapping("/sendSms")
    public Result sendSms(@RequestParam("phone") String phone, String type) {
        userService.sendSms(phone, type);
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

        Map userInfo = new HashMap();

        //密码登录
        if (user.getCode() == null) {
            if (userService.checkUser(user)) {
                userInfo = userService.getUserInfo(user.getUsername());
            }
        } else { //验证码登录
            if (userService.loginByCode(user)) {
                userInfo = userService.getUserInfo(user.getUsername());
            }
        }
        return userInfo;
    }
}
