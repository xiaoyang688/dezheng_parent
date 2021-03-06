package com.dezheng.controller.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.aliyun.oss.OSSClient;
import com.dezheng.entity.Result;
import com.dezheng.pojo.user.FaceResult;
import com.dezheng.pojo.user.User;
import com.dezheng.service.user.FaceAccessService;
import com.dezheng.service.user.UserService;
import com.dezheng.utils.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private OSSClient ossClient;

    @Reference
    private UserService userService;

    @Reference
    private FaceAccessService faceAccessService;

    @GetMapping("/sendSms")
    public Result sendSms(@RequestParam("phone") String phone, String type) {
        userService.sendSms(phone, type);
        return new Result(1, "发送验证码成功！");
    }

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        String password = user.getPassword();
        String gensalt = BCrypt.gensalt();
        String hashpw = BCrypt.hashpw(password, gensalt);
        user.setPassword(hashpw);
        userService.register(user, user.getCode());
        return new Result(1, "注册成功！");
    }

    @PostMapping("/signIn")
    public Map signIn(@RequestBody User user) {

        Map userInfo = new HashMap();

        //密码登录
        if (user.getCode() == null) {
            if (userService.checkUser(user)) {
                userInfo = userService.getUserInfo(user.getUsername());
            }
        } else { //验证码登录
            if (userService.loginByCode(user)) {
                userInfo = userService.getUserInfo(user.getUsername());
            }
        }
        return userInfo;
    }

    @PostMapping("/faceLogin")
    public Map faceLogin(@RequestParam("file") String image_base64, HttpServletRequest request) {

        FaceResult faceResult = faceAccessService.searchFace(image_base64);
        //相识度达到80%
        Map map = new HashMap();
        System.out.println(faceResult.getConfidence());
        int i = faceResult.getConfidence().compareTo(new BigDecimal(75));
        if (i == 1) {
            Map userInfo = userService.getUserInfo(faceResult.getUserName());
            return userInfo;
        } else {
            map.put("code", 0);
            map.put("message", "人脸识别失败");
        }
        return map;
    }

}
