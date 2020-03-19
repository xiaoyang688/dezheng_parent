package com.dezheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.dezheng.dao.AddressMapper;
import com.dezheng.dao.UserMapper;
import com.dezheng.pojo.user.Address;
import com.dezheng.pojo.user.User;
import com.dezheng.redis.CacheKey;
import com.dezheng.service.user.UserService;
import com.dezheng.utils.BCrypt;
import com.dezheng.utils.IdWorker;
import com.dezheng.utils.JWTUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private IdWorker idWorker;

    public void sendSms(String phone) {

        //校验
        if (phone.length() < 10) {
            throw new RuntimeException("手机号码不正确！");
        }

        Random random = new Random();
        int code = random.nextInt(9999);
        System.out.println(code);
        //生成少于四位
        if (code <= 999) {
            code += 1000;
        }
        //将验证码存入redis
        redisTemplate.boundValueOps(phone).set(code + "");
        redisTemplate.boundValueOps("phone").expire(5, TimeUnit.MINUTES);

        //发送mq信息,使用直接模式
        Map codeMap = new HashMap();
        codeMap.put("phone", phone);
        codeMap.put("code", code + "");
        rabbitTemplate.convertAndSend("", "queue.sms", JSON.toJSONString(codeMap));
    }

    @Override
    public void register(User user, String code) {
        //校验
        if (user.getUsername() == null) {
            throw new RuntimeException("请输入正确手机号码");
        }
        if (user.getPassword().length() < 5) {
            throw new RuntimeException("密码少于六位");
        }
        if ("".equals(code) || code == null) {
            throw new RuntimeException("验证码为空");
        }
        //查询是否有存在用户名
        User searchUser = userMapper.selectByPrimaryKey(user.getUsername());

        if (searchUser != null) {//用户已存在
            throw new RuntimeException("用户已存在");
        } else {//用户不存在

            //获取系统验证码
            String sysCode = (String) redisTemplate.boundValueOps(user.getUsername()).get();

            //获取验证码为空
            if (sysCode == null) {
                throw new RuntimeException("验证码失效了");
            }

            System.out.println(sysCode);
            System.out.println(code);

            //校验验证码
            if (sysCode.equals(code)) {
                //验证成功
                user.setCreateTime(new Date());
                user.setUpdateTime(new Date());
                user.setPhone(user.getUsername());
                user.setIsEmailCheck("0");
                user.setStatus("1");
                user.setHeadPic("https://dss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=2471723103,4261647594&fm=26&gp=0.jpg");
                userMapper.insertSelective(user);
            } else {
                throw new RuntimeException("验证码错误");
            }
        }
    }

    @Override
    public boolean checkUser(User user) {

        User searchUser = userMapper.selectByPrimaryKey(user.getUsername());
        if (searchUser == null) {
            throw new RuntimeException("用户未注册");
        }
        //检验密码
        if (BCrypt.checkpw(user.getPassword(), searchUser.getPassword())) {
            return true;
        } else {
            throw new RuntimeException("密码错误");
        }
    }

    @Override
    public Map getUserInfo(String username) {

        //生成token
        String token = JWTUtils.buildJWT(username);
        //查询用户信息
        User user = userMapper.selectByPrimaryKey(username);
        //封装用户信息
        Map result = new HashMap();

        result.put("username", user.getUsername());
        result.put("haedPic", user.getHeadPic());
        result.put("token", token);
        return result;
    }

    @Override
    public String getUserName(String token) {
        System.out.println(token);
        token = token.replace("Bearer ", "");
        return JWTUtils.vaildToken(token);
    }

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

        if (i < 1){
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
