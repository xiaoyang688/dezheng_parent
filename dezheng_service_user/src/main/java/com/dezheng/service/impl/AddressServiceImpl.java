package com.dezheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dezheng.dao.AddressMapper;
import com.dezheng.pojo.user.Address;
import com.dezheng.service.user.AddressService;
import com.dezheng.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private AddressMapper addressMapper;


    public List<Address> findAddressList(String username) {
        Example example = new Example(Address.class);
        example.createCriteria()
                .andEqualTo("username", username);
        List<Address> addressList = addressMapper.selectByExample(example);
        return addressList;
    }

    @Override
    public void addAddress(Address address) {
        //查找地址
        List<Address> addressList = findAddressList(address.getUsername());

        address.setIsDefault("0");
        if (addressList.size() == 0) {
            address.setIsDefault("1");
        }
        System.out.println(address.getPhone().length());
        if (address.getPhone().length() != 11) {
            throw new RuntimeException("输入手机号码不正确");
        }
        address.setId(idWorker.nextId() + "");
        addressMapper.insertSelective(address);
    }

    public void updateDefAddress(String username, String id) {

        List<Address> addressList = findAddressList(username);
        List<Address> addr = addressList.stream().filter(address -> address.getIsDefault().equals("1")).collect(Collectors.toList());
        //没有设置默认地址
        if (addr == null) {
            addr = new ArrayList<>();
        }

        if (addr.size() == 1) {
            Address defaultAddress = addr.get(0);
            //将原来的默认地址改为不默认
            if (defaultAddress.getId().equals(id)) {
                throw new RuntimeException("已经是默认地址");
            }
            defaultAddress.setIsDefault("0");
            addressMapper.updateByPrimaryKey(defaultAddress);
        }

        //改为默认地址
        Address modifyAddress = addressMapper.selectByPrimaryKey(id);
        modifyAddress.setIsDefault("1");
        addressMapper.updateByPrimaryKey(modifyAddress);
    }

    @Override
    public void updateAddress(Address address) {

        Address searchAddress = addressMapper.selectByPrimaryKey(address.getId());
        searchAddress.setContact(address.getContact());
        if (address.getPhone().length() != 11) {
            throw new RuntimeException("输入手机号码不正确");
        }
        searchAddress.setPhone(address.getPhone());
        searchAddress.setAddress(address.getAddress());
        searchAddress.setDetail(address.getDetail());
        int i = addressMapper.updateByPrimaryKeySelective(searchAddress);

        if (i < 1) {
            throw new RuntimeException("修改失败");
        }
    }

    @Override
    public Address findAddressById(String id) {
        Address address = addressMapper.selectByPrimaryKey(id);
        if (address == null) {
            throw new RuntimeException("地址不存在");
        }
        return address;
    }

    public void deleteAddress(String id) {
        int i = addressMapper.deleteByPrimaryKey(id);
        if (i < 1) {
            throw new RuntimeException("地址已删除");
        }
    }

}
