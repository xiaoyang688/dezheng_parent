package com.dezheng.service.user;

import com.dezheng.pojo.user.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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

    /**
     * 校验密码
     * @param user
     * @return
     */
    public boolean checkUser(User user);

    /**
     * 获取用户信息
     * @return
     */
    public Map getUserInfo(String username);

    /**
     * 获取用户名
     * @return
     */
    public String getUserName(String token);

}
