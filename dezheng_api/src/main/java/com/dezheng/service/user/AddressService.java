package com.dezheng.service.user;

import com.dezheng.pojo.user.Address;

import java.util.List;

public interface AddressService {


    public List<Address> findAddressList(String username);

    /**
     * 添加地址
     *
     * @param address
     */
    public void addAddress(Address address);

    /**
     * 更新默认地址￿
     *
     * @param username
     * @param id
     */
    public void updateDefAddress(String username, String id);

    /**
     * 更新地址
     *
     * @param address
     */
    public void updateAddress(Address address);

    /**
     * 通过id查找地址
     *
     * @param id
     * @return
     */
    public Address findAddressById(String id);

    /**
     * 删除地址
     *
     * @param id
     * @param id
     */
    public void deleteAddress(String id);

}
