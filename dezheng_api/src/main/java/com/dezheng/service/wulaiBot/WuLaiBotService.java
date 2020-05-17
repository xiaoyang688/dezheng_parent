package com.dezheng.service.wulaiBot;

public interface WuLaiBotService {
    /**
     * 创建用户
     * @param username
     */
    public void createUser(String username);

    /**
     * 获取机器人回复
     * @param username
     * @param answer
     * @return
     */
    public String getAnswer(String username, String answer);
}
