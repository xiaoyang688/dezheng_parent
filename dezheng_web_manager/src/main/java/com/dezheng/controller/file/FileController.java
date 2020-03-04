package com.dezheng.controller.file;

import com.aliyun.oss.OSSClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping("/oss")
public class FileController {

    @Autowired
    private OSSClient ossClient;

    @PostMapping("/upload")
    public String oss(@RequestParam("file") MultipartFile file, String folder) throws IOException {

        String bucketName = "xiaoyang688";

        //获取文件名
        String fileName = folder + "/" + UUID.randomUUID().toString().replace("-", "") + file.getOriginalFilename();

        ossClient.putObject(bucketName, fileName, file.getInputStream());

        return "https://" + bucketName + ".oss-cn-beijing.aliyuncs.com/" + fileName;

    }

}
