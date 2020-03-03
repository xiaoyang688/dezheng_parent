package com.dezheng.entity;

import java.io.Serializable;

public class Result implements Serializable {

    private Integer code; //成功状态码 成功：1 ,失败：0
    private String message; //错误信息

    /**
     * 出现错误时调用此构造方法
     *
     * @param code
     * @param message
     */
    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result() {
        this.code = 1;
        this.message = "执行成功";
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
