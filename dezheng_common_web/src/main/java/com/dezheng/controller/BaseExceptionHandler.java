package com.dezheng.controller;

import com.dezheng.entity.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class BaseExceptionHandler {
    /**
     * 公共异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e) {
        e.printStackTrace();
        System.out.println("出现异常了！！！");
        if (e.getMessage().equals("Token 过期") || e.getMessage().equals("Token 无效")) {
            return new Result(4, e.getMessage());
        }
        System.out.println("我进入该方法啦");
        return new Result(0, e.getMessage());
    }
}
