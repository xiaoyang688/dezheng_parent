package com.dezheng.controller.goods;

import com.alibaba.dubbo.config.annotation.Reference;
import com.aliyun.oss.OSSClient;
import com.dezheng.entity.Result;
import com.dezheng.pojo.goods.Album;
import com.dezheng.service.goods.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/album")
public class AlbumController {

    @Reference
    private AlbumService albumService;

    @Autowired
    private OSSClient ossClient;

    @PostMapping("/addCover")
    public Result addCover(@RequestParam("file") MultipartFile file, String title) {

        String bucketName = "xiaoyang688";
        String fileName = "album/" + UUID.randomUUID().toString().replace("-", "") + file.getOriginalFilename();

        //上传到OSS
        String imageUrl = upload(file, bucketName, fileName);

        //添加封面
        Album album = new Album();
        album.setTitle(title);
        album.setImage(imageUrl);
        albumService.addCover(album);
        return new Result(1, "上传成功");
    }


    @PostMapping("/addImageItem")
    public Result addImageItem(@RequestParam("file") MultipartFile file, Integer id){

        String bucketName = "xiaoyang688";
        String fileName = "album/" + UUID.randomUUID().toString().replace("-", "") + file.getOriginalFilename();
        //上传到OSS
        String imageUrl = upload(file, bucketName, fileName);

        albumService.addImageItem(id, imageUrl);

        return new Result(1, "上传成功");
    }

    private String upload(MultipartFile file, String bucketName, String fileName){
        try {
            ossClient.putObject(bucketName, fileName, file.getInputStream());
            String imageUrl = "https://" + bucketName + ".oss-cn-beijing.aliyuncs.com/" + fileName;
            return imageUrl;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
