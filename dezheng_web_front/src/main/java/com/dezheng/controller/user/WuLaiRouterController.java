package com.dezheng.controller.user;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/wulai")
public class WuLaiRouterController {
    @PostMapping("/getMessage")
    public void getMessage(@RequestBody Map map) {
        System.out.println(map);
    }
}
