package com.dezheng.controller.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dezheng.service.user.UserService;
import com.dezheng.service.wulaiBot.WuLaiBotService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    public Map getAnswer(@RequestBody String question, HttpServletRequest request) {
        String userName = userService.getUserName(request.getHeader("Authorization"));
        //创建用户
        wuLaiBotService.createUser(userName);
        JSONObject questionJson = JSONObject.parseObject(question);
        question = (String) questionJson.get("question");
        //获取答案
        Map map = new HashMap();
        map.put("answer", wuLaiBotService.getAnswer(userName, question));
        return map;
    }

}
