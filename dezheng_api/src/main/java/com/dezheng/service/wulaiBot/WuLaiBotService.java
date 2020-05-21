package com.dezheng.service.wulaiBot;

import java.util.Map;

public interface WuLaiBotService {
    /**
     * 创建用户
     *
     * @param username
     */
    public void createUser(String username);

    /**
     * 获取机器人回复
     *
     * @param username
     * @param question
     * @return
     */
    public String getAnswer(String username, String question);

    /**
     * 获取人工服务消息,发送message给前端
     * @param message
     */
    public void sendMessage(Map message);

    /**
     * 接受消息
     * @param username
     */
    public void receiveMessage(String username, String question);

    /**
     * 语音识别
     * @param voice
     * @return
     */
    public String voiceToText(byte[] voice);
}
