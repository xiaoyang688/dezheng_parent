package com.dezheng.controller.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.dezheng.entity.Result;
import com.dezheng.service.user.UserService;
import com.dezheng.service.wulaiBot.WuLaiBotService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class WuLaiController {

    @Reference
    private WuLaiBotService wuLaiBotService;

    @Reference
    private UserService userService;

    @GetMapping("/wulai")
    public void createUser(HttpServletRequest request) {
        String userName = userService.getUserName(request.getHeader("Authorization"));
        wuLaiBotService.createUser(userName);
    }

    @PostMapping("/wulaiBot/getAnswer")
    public Map getAnswer(@RequestParam(value = "file", required = false) MultipartFile file, @RequestBody(required = false) String question, HttpServletRequest request) {
        if (file == null && question == null) {
            throw new RuntimeException("请求参数不能为空！");
        }
        String userName = userService.getUserName(request.getHeader("Authorization"));
        //创建用户
        wuLaiBotService.createUser(userName);
        //判断用户是否发送语音
        if (file != null) {
            try {
                question = wuLaiBotService.voiceToText(file.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("转换字节流失败");
            }
        } else {
            JSONObject questionJson = JSONObject.parseObject(question);
            question = (String) questionJson.get("question");
        }
        String answer = wuLaiBotService.getAnswer(userName, question);
        //获取答案
        Map map = new HashMap();
        map.put("answer", answer);
        return map;
    }

    @PostMapping("/wulaiBot/sendMessage")
    public Result sendMessage(@RequestParam(value = "file", required = false) MultipartFile file, @RequestBody(required = false) String question, HttpServletRequest request) {
        if (file == null && question == null) {
            throw new RuntimeException("请求参数不能为空！");
        }
        String userName = userService.getUserName(request.getHeader("Authorization"));
        //创建用户
        wuLaiBotService.createUser(userName);
        if (file != null) {
            try {
                byte[] bytes = file.getBytes();
                question = wuLaiBotService.voiceToText(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            JSONObject questionJson = JSONObject.parseObject(question);
            question = (String) questionJson.get("question");
        }
        wuLaiBotService.receiveMessage(userName, question);
        return new Result(1, "发送成功");
    }

}
