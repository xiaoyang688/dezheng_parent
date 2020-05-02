package com.dezheng.controller.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.aliyun.oss.OSSClient;
import com.dezheng.entity.Result;
import com.dezheng.pojo.user.Address;
import com.dezheng.pojo.user.CollectInfo;
import com.dezheng.pojo.user.User;
import com.dezheng.service.tulingBot.TuLingBotService;
import com.dezheng.service.user.AddressService;
import com.dezheng.service.user.UserService;
import com.dezheng.utils.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    @Reference
    private AddressService addressService;

    @Reference
    private TuLingBotService tuLingBotService;

    @Autowired
    private OSSClient ossClient;

    @GetMapping("/findAddressList")
    public List<Address> findAddressList(HttpServletRequest request) {
        String userName = userService.getUserName(request.getHeader("Authorization"));
        return addressService.findAddressList(userName);
    }

    @PostMapping("/addAddress")
    public Result addAddress(@RequestBody Address address, HttpServletRequest request) {
        String username = userService.getUserName(request.getHeader("Authorization"));
        address.setUsername(username);
        addressService.addAddress(address);
        return new Result(1, "添加成功");
    }

    @GetMapping("/updateDefaultAddress")
    public Result updateDefaultAddress(HttpServletRequest request, String id) {
        String username = userService.getUserName(request.getHeader("Authorization"));
        System.out.println("进入此方法");
        addressService.updateDefAddress(username, id);
        return new Result(1, "更新成功");
    }

    @GetMapping("/deleteAddress")
    public Result deleteAddress(String id) {
        addressService.deleteAddress(id);
        return new Result(1, "删除成功");
    }

    @GetMapping("/findAddressById")
    public Address findAddressById(String id) {
        Address address = addressService.findAddressById(id);
        return address;
    }

    @PostMapping("/updateAddress")
    public Result updateAddress(@RequestBody Address address) {
        addressService.updateAddress(address);
        return new Result(1, "修改成功");
    }

    @GetMapping("/getUsername")
    public Map getUsername(HttpServletRequest request) {
        String userName = userService.getUserName(request.getHeader("Authorization"));
        Map map = new HashMap();
        map.put("username", userName);
        return map;
    }

    @PostMapping("/modifyPassword")
    public Result modifyPassword(@RequestBody User user, HttpServletRequest request) {
        String username = userService.getUserName(request.getHeader("Authorization"));
        //判断是否当前用户
        if (!user.getUsername().equals(username)) {
            throw new RuntimeException("只能修改当前用户密码");
        }

        //密码加密
        String password = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        userService.modifyPassword(username, user.getCode(), password);

        return new Result(1, "修改密码成功");
    }

    @GetMapping("/deleteUser")
    public Result deleteUserByUserName(String username, HttpServletRequest request) {
        //获取当前登录用户
        String loginUser = userService.getUserName(request.getHeader("Authorization"));

        //校验当前用户
        if (!username.equals(loginUser)) {
            throw new RuntimeException("只能注销当前用户");
        }

        userService.deleteUserByUsername(username);
        return new Result(1, "注销成功");
    }

    @GetMapping("/getHeadPicList")
    public List<String> getHeadPicList() {
        return userService.getHeadPicList();
    }

    @PostMapping("/updateHeadPic")
    public Result updateHeadPic(@RequestBody User user, HttpServletRequest request) {
        String userName = userService.getUserName(request.getHeader("Authorization"));
        user.setUsername(userName);
        userService.updateHeadPic(user);
        return new Result(1, "修改成功");
    }

    @PostMapping("/suggest")
    public Result suggest(@RequestBody Map<String, Object> suggestMap, HttpServletRequest request) {

        String userName = userService.getUserName(request.getHeader("Authorization"));
        suggestMap.put("username", userName);

        userService.suggest(suggestMap);
        return new Result(1, "您的宝贵意见我们已经收到");
    }

    @PostMapping("/collectInfo")
    public Map<String, Object> collectInfo(@RequestBody Map<String, String> info, HttpServletRequest request) {

        String userName = userService.getUserName(request.getHeader("Authorization"));
        CollectInfo collectInfo = new CollectInfo();
        //保存用户名
        collectInfo.setUsername(userName);
        //保存答案
        collectInfo.setAnswer(JSON.toJSONString(info));

        return userService.collectInfo(collectInfo);

    }

    @GetMapping("/findCollectInfo")
    public Map<String, Object> findCollectInfo(HttpServletRequest request) {
        String userName = userService.getUserName(request.getHeader("Authorization"));
        return userService.findCollectInfo(userName);
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {

        String userName = userService.getUserName(request.getHeader("Authorization"));

        String bucketName = "xiaoyang688";
        //获取文件名
        String filename = file.getOriginalFilename();
        //获取文件后缀
        String suffix = filename.substring(filename.lastIndexOf("."));
        //获取时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = sdf.format(new Date());

        //拼接文件名
        String uploadFile = "suggest/" + userName + "_" + date + suffix;

        try {
            ossClient.putObject(bucketName, uploadFile, file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("上传失败");
        }

        return "https://" + bucketName + ".oss-cn-beijing.aliyuncs.com/" + uploadFile;
    }

    @PostMapping("/chatBot")
    public Map chatBot(@RequestBody Map<String, String> question, HttpServletRequest request) {
        String userName = userService.getUserName(request.getHeader("Authorization"));
        question.put("username", userName);
        return tuLingBotService.getAnswer(question);
    }

}
