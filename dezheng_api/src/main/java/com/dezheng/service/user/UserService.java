package com.dezheng.service.user;

import com.dezheng.pojo.user.Suggest;
import com.dezheng.pojo.user.User;

import java.util.List;
import java.util.Map;

public interface UserService {

    /**
     * 发送注册验证码
     * @param phone
     */
    public void sendSms(String phone, String type);

    /**
     * 用户注册
     * @param user
     * @param code
     */
    public void register(User user, String code);

    /**
     * 验证码登录
     * @param user
     */
    public boolean loginByCode(User user);

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

    /**
     * 修改密码
     * @param username
     * @param password
     */
    public void modifyPassword(String username, String code, String password);

    /**
     * 注销用户
     * @param username
     */
    public void deleteUserByUsername(String username);

    /**
     * 获取所有头像
     * @return
     */
    public List<String> getHeadPicList();

    /**
     * 更新头像
     * @param user
     */
    public void updateHeadPic(User user);

    /**
     * 用户提建议
     * @param suggest
     */
    public void suggest(Suggest suggest);

}
