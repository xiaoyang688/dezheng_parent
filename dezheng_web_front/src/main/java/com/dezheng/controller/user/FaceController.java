package com.dezheng.controller.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.aliyun.oss.OSSClient;
import com.dezheng.entity.Result;
import com.dezheng.service.user.FaceAccessService;
import com.dezheng.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class FaceController {

    @Reference
    private UserService userService;

    @Reference
    private FaceAccessService faceAccessService;

    @Autowired
    private OSSClient ossClient;

    @PostMapping("/addFace")
    public Result addFace(@RequestParam("file") String file, HttpServletRequest request) {

        String userName = userService.getUserName(request.getHeader("Authorization"));

        System.out.println(file);

        faceAccessService.addFace(userName, file);

        return new Result(1, "人脸录入成功");
    }

    @GetMapping("/deleteFace")
    public Result deleteFace(HttpServletRequest request) {
        String userName = userService.getUserName(request.getHeader("Authorization"));
        faceAccessService.deleteFace(userName);
        return new Result(1, "删除成功");
    }

}
