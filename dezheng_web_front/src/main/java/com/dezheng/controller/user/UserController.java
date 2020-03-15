package com.dezheng.controller.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dezheng.pojo.user.Address;
import com.dezheng.service.user.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    @GetMapping("/getUsername")
    public Map getUsername(HttpServletRequest request){
        String userName = userService.getUserName(request.getHeader("Authorization"));
        Map map = new HashMap();
        map.put("username", userName);
        return map;
    }

    @GetMapping("/findAddressList")
    public List<Address> findAddressList(HttpServletRequest request){
        String userName = userService.getUserName(request.getHeader("Authorization"));
        return userService.findAddressByUsername(userName);
    }

}
