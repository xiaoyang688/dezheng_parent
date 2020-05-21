package com.dezheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dezheng.dao.CollectInfoMapper;
import com.dezheng.dao.SuggestMapper;
import com.dezheng.dao.UserMapper;
import com.dezheng.pojo.user.CollectInfo;
import com.dezheng.pojo.user.Suggest;
import com.dezheng.pojo.user.User;
import com.dezheng.service.user.UserService;
import com.dezheng.utils.BCrypt;
import com.dezheng.utils.IdWorker;
import com.dezheng.utils.JWTUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SuggestMapper suggestMapper;

    @Autowired
    private CollectInfoMapper collectInfoMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void sendSms(String phone, String type) {

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

        saveAndSendCode(phone, code, type);

    }

    /**
     * 保存验证码到redis并发送rabbitMQ
     *
     * @param phone
     * @param code
     * @param type
     */
    private void saveAndSendCode(String phone, Integer code, String type) {

        if (type.equals("register") || type.equals("modifyPassword") || type.equals("login")) {
            //将验证码存入redis
            String redisKey = type + "_" + phone;
            redisTemplate.boundValueOps(redisKey).set(code + "");
            redisTemplate.boundValueOps(redisKey).expire(5, TimeUnit.MINUTES);

            //发送mq信息,使用直接模式
            Map codeMap = new HashMap();
            codeMap.put("phone", phone);
            codeMap.put("code", code + "");
            rabbitTemplate.convertAndSend("", type + "Sms", JSON.toJSONString(codeMap));
        } else {
            throw new RuntimeException("发送验证码类型错误");
        }
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
            String sysCode = (String) redisTemplate.boundValueOps("register_" + user.getUsername()).get();

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
                user.setHeadPic("https://xiaoyang688.oss-cn-beijing.aliyuncs.com/headPic/headpic1.jpg");
                userMapper.insertSelective(user);
            } else {
                throw new RuntimeException("验证码错误");
            }
        }
    }

    @Override
    public boolean loginByCode(User user) {
        User searchUser = userMapper.selectByPrimaryKey(user.getUsername());
        //校验用户是否存在
        if (searchUser == null) {
            throw new RuntimeException("用户未注册或已注销，请重新注册");
        }

        String sysCode = (String) redisTemplate.boundValueOps("login_" + user.getUsername()).get();
        //校验系统验证码
        if (sysCode == null) {
            throw new RuntimeException("验证码过期");
        } else if (user.getCode().equals(sysCode)) {
            return true;
        } else {
            throw new RuntimeException("验证码错误");
        }

    }

    @Override
    public boolean checkUser(User user) {

        User searchUser = userMapper.selectByPrimaryKey(user.getUsername());
        if (searchUser == null) {
            throw new RuntimeException("用户未注册或已注销，请重新注册");
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
        if (token == null) {
            throw new RuntimeException("用户未登录");
        }
        token = token.replace("Bearer ", "");
        return JWTUtils.vaildToken(token);
    }


    @Override
    public void modifyPassword(String username, String code, String password) {

        User user = userMapper.selectByPrimaryKey(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        //校验验证码
        String sysCode = (String) redisTemplate.boundValueOps("modifyPassword_" + username).get();

        if (sysCode == null) {
            throw new RuntimeException("验证码过期");
        }

        if (sysCode.equals(code)) {
            //修改密码
            user.setPassword(password);
            userMapper.updateByPrimaryKeySelective(user);
        } else {
            throw new RuntimeException("验证码错误");
        }

    }

    @Override
    public void deleteUserByUsername(String username) {
        int i = userMapper.deleteByPrimaryKey(username);
        if (i < 1) {
            throw new RuntimeException("用户不存在或用户已删除");
        }
    }

    @Override
    public List<String> getHeadPicList() {

        List<String> headPicList = new ArrayList<>();
        headPicList.add("https://xiaoyang688.oss-cn-beijing.aliyuncs.com/headPic/headpic1.jpg");
        headPicList.add("https://xiaoyang688.oss-cn-beijing.aliyuncs.com/headPic/headpic2.jpg");
        headPicList.add("https://xiaoyang688.oss-cn-beijing.aliyuncs.com/headPic/headpic3.jpg");
        headPicList.add("https://xiaoyang688.oss-cn-beijing.aliyuncs.com/headPic/headpic4.jpg");
        headPicList.add("https://xiaoyang688.oss-cn-beijing.aliyuncs.com/headPic/headpic5.jpg");
        headPicList.add("https://xiaoyang688.oss-cn-beijing.aliyuncs.com/headPic/headpic6.jpg");
        headPicList.add("https://xiaoyang688.oss-cn-beijing.aliyuncs.com/headPic/headpic7.jpg");
        headPicList.add("https://xiaoyang688.oss-cn-beijing.aliyuncs.com/headPic/headpic8.jpg");
        headPicList.add("https://xiaoyang688.oss-cn-beijing.aliyuncs.com/headPic/headpic9.jpg");

        return headPicList;
    }

    @Override
    public void updateHeadPic(User user) {
        //校验用户
        User searchUser = userMapper.selectByPrimaryKey(user.getUsername());
        if (searchUser == null) {
            throw new RuntimeException("当前用户不存在");
        }

        //更新头像
        String headPic = user.getHeadPic();
        if (headPic == null) {
            throw new RuntimeException("头像不能为空");
        }
        searchUser.setHeadPic(headPic);
        int i = userMapper.updateByPrimaryKey(searchUser);

        if (i < 1) {
            throw new RuntimeException("更换失败");
        }
    }

    @Override
    public void suggest(Map<String, Object> suggestMap) {

        //获取用户名
        String username = (String) suggestMap.get("username");

        //获取意见
        String suggest = (String) suggestMap.get("suggest");
        if (suggest == null || suggest.equals("")) {
            throw new RuntimeException("意见不能为空");
        }

        //获取上传图片链接
        JSONArray imageList = (JSONArray) suggestMap.get("imageList");
        if (imageList == null) {
            imageList = new JSONArray();
        }
        List<String> images = new ArrayList<>();

        for (Object image : imageList) {
            Map map = JSONObject.parseObject(image.toString(), Map.class);
            String url = (String) map.get("url");
            images.add(url);
        }
        //清洗图片链接
        String image = StringUtils.strip(images.toString(), "[]");

        //持久化
        Suggest saveSuggest = new Suggest();
        saveSuggest.setId(idWorker.nextId() + "");
        saveSuggest.setUsername(username);
        saveSuggest.setSuggest(suggest);
        saveSuggest.setImage(image);

        suggestMapper.insert(saveSuggest);

    }

    @Override
    public Map<String, Object> collectInfo(CollectInfo info) {

        //查询信息采集
        CollectInfo searchCollectInfo = collectInfoMapper.selectByPrimaryKey(info.getUsername());

        //校验是否信息采集过期
        if (searchCollectInfo != null) {
            long currentTime = new Date().getTime();
            if (currentTime > searchCollectInfo.getExpireTime().getTime()) { //信息采集过期,重新采集
                //重新获取起始时间
                searchCollectInfo.setStartTime(new Date());
                //重新获取过期时间
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MINUTE, 1);
                searchCollectInfo.setExpireTime(calendar.getTime());
                //设置状态为0
                searchCollectInfo.setStatus("0");
                collectInfoMapper.updateByPrimaryKeySelective(searchCollectInfo);
                Map<String, Object> result = JSON.parseObject(searchCollectInfo.getAnswer(), Map.class);
                result.put("createTime", searchCollectInfo.getStartTime());
                result.put("status", searchCollectInfo.getStatus());
                return result;
            } else { //信息采集在有效期内
                throw new RuntimeException("信息采集已提交,请勿重复提交");
            }
        } else { //还没填写信息采集
            //获取起始时间
            info.setStartTime(new Date());
            //获取过期时间
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 1);
            info.setExpireTime(calendar.getTime());
            //设置状态
            info.setStatus("0");
            collectInfoMapper.insertSelective(info);
            //返回信息采集
            Map<String, Object> result = JSON.parseObject(info.getAnswer(), Map.class);
            result.put("createTime", info.getStartTime());
            result.put("status", info.getStatus());
            return result;
        }
    }


    @Override
    public Map<String, Object> findCollectInfo(String username) {

        CollectInfo collectInfo = collectInfoMapper.selectByPrimaryKey(username);
        //校验信息采集
        if (collectInfo == null) {
            throw new RuntimeException("您还没有填写信息采集");
        }

        Date currentTime = new Date();

        if (currentTime.getTime() > collectInfo.getExpireTime().getTime()) {
            Map<String, Object> result = new HashMap<>();
            result.put("status", "1");
            result.put("massage", "信息采集已过期，请重新填写");
            return result;
        }

        String answer = collectInfo.getAnswer();
        Map<String, Object> result = JSON.parseObject(answer, Map.class);

        result.put("status", collectInfo.getStatus());
        result.put("createTime", collectInfo.getStartTime());
        return result;
    }
}
