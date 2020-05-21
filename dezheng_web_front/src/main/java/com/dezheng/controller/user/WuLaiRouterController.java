package com.dezheng.controller.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dezheng.service.wulaiBot.WuLaiBotService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/wulai")
public class WuLaiRouterController {

    @Reference
    private WuLaiBotService wuLaiBotService;

    @PostMapping("/getMessage")
    public void getMessage(@RequestBody Map message) {
        wuLaiBotService.sendMessage(message);
    }

}
