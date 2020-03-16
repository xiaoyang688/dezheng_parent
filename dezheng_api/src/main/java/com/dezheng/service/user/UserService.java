package com.dezheng.service.user;

import com.dezheng.pojo.user.Address;
import com.dezheng.pojo.user.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
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

    public List<Address> findAddressList(String username);

    /**
     * 添加地址
     * @param address
     */
    public void addAddress(Address address);

    /**
     * 更新默认地址￿
     * @param username
     * @param id
     */
    public void updateDefAddress(String username, String id);

    /**
     * 更新地址
     * @param address
     */
    public void updateAddress(Address address);

    /**
     * 通过id查找地址
     * @param id
     * @return
     */
    public Address findAddressById(String id);

    /**
     * 删除地址
     * @param id
     * @param id
     */
    public void deleteAddress(String id);

}
