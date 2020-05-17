package com.dezheng;

public enum WuLaiTypeEnum {
    //创建用户
    CREATE_USER("https://openapi.wul.ai/v2/user/create"),

    FIND_USER("https://openapi.wul.ai/v2/user/get"),

    GET_ANSWER("https://openapi.wul.ai/v2/msg/bot-response");

    private String url;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private WuLaiTypeEnum(String url) {
        this.url = url;
    }
}
