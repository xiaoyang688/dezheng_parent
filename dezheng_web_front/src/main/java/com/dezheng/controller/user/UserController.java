package com.dezheng.controller.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dezheng.entity.Result;
import com.dezheng.pojo.user.User;
import com.dezheng.service.user.UserService;
import com.dezheng.utils.BCrypt;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/login")
    public Result login(HttpServletRequest request, HttpServletResponse response, String username, String password){
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            System.out.println(cookie.getValue());
        }
        String token = UUID.randomUUID().toString();
        response.addCookie(new Cookie("token", token));
        return new Result();
    }

    @GetMapping("/test")
    public Map test(HttpServletRequest request){
        Map result = new HashMap();
        String authorization = request.getHeader("Authorization");

        return result;
    }

}
