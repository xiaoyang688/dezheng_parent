package com.dezheng.service.user;

import com.dezheng.pojo.user.User;

public interface UserService {

    /**
     * 发送验证码
     * @param phone
     */
    public void sendSms(String phone);

    /**
     * 用户注册
     * @param user
     * @param code
     */
    public void register(User user, String code);

}
