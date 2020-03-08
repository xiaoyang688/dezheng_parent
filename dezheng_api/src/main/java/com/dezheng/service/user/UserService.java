package com.dezheng.service.user;

public interface UserService {

    /**
     * 发送验证码
     * @param phone
     */
    public void sendSms(String phone);

}
