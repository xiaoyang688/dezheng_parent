package com.dezheng.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        System.out.println("开始拦截！！");

        String authorization = request.getHeader("Authorization");
        //检验token
        if (authorization == null) {
            throw new RuntimeException("用户未登陆");
        }
        return true;
    }

}
