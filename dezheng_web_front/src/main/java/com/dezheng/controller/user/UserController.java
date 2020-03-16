package com.dezheng.controller.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dezheng.entity.Result;
import com.dezheng.pojo.user.Address;
import com.dezheng.service.user.UserService;
import org.springframework.web.bind.annotation.*;

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
    public Map getUsername(HttpServletRequest request) {
        String userName = userService.getUserName(request.getHeader("Authorization"));
        Map map = new HashMap();
        map.put("username", userName);
        return map;
    }

    @GetMapping("/findAddressList")
    public List<Address> findAddressList(HttpServletRequest request) {
        String userName = userService.getUserName(request.getHeader("Authorization"));
        return userService.findAddressList(userName);
    }

    @PostMapping("/addAddress")
    public Result addAddress(@RequestBody Address address, HttpServletRequest request) {
        String username = userService.getUserName(request.getHeader("Authorization"));
        address.setUsername(username);
        userService.addAddress(address);
        return new Result(1, "添加成功");
    }

    @GetMapping("/updateDefaultAddress")
    public Result updateDefaultAddress(HttpServletRequest request, String id) {
        String username = userService.getUserName(request.getHeader("Authorization"));
        System.out.println("进入此方法");
        userService.updateDefAddress(username, id);
        return new Result(1, "更新成功");
    }

    @GetMapping("/deleteAddress")
    public Result deleteAddress(String id) {
        userService.deleteAddress(id);
        return new Result(1, "删除成功");
    }

    @GetMapping("/findAddressById")
    public Address findAddressById(String id) {
        Address address = userService.findAddressById(id);
        return address;
    }

    @PostMapping("/updateAddress")
    public Result updateAddress(@RequestBody Address address) {
        userService.updateAddress(address);
        return new Result(1, "修改成功");
    }

}
