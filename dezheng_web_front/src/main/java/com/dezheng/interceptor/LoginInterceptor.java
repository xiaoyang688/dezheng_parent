package com.dezheng.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate redisTemplate;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        System.out.println("开始拦截！！");

        String authorization = request.getHeader("Authorization");
        String[] s = authorization.split(" ");
        String s1 = s[1];
        String o = (String) redisTemplate.boundValueOps(s1).get();
        if (s1.equals(o)) {
            throw new RuntimeException("用户未登录");
        }
        System.out.println(o);
        System.out.println(s1);
        return true;
    }

}
