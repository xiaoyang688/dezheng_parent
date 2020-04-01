package com.dezheng.service.tulingBot;

import java.util.Map;

public interface TuLingBotService {

    /**
     * 获取机器人回复
     * @return
     */
    public Map<String, Object> getAnswer(Map<String, String> question);

}
