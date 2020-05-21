package com.dezheng;

public enum WuLaiTypeEnum {
    //创建用户
    CREATE_USER("https://openapi.wul.ai/v2/user/create"),
    //查找用户
    FIND_USER("https://openapi.wul.ai/v2/user/get"),
    //获取机器人回复
    GET_ANSWER("https://openapi.wul.ai/v2/msg/bot-response"),
    //接受消息
    RECEIVE_MESSAGE("https://openapi.wul.ai/v2/msg/receive");

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
