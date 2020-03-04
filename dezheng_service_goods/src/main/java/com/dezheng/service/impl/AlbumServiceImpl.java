package com.dezheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONArray;
import com.dezheng.dao.AlbumMapper;
import com.dezheng.pojo.goods.Album;
import com.dezheng.service.goods.AlbumService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AlbumServiceImpl implements AlbumService {

    @Autowired
    private AlbumMapper albumMapper;

    @Override
    public Album findById(Integer id) {

        return albumMapper.selectByPrimaryKey(id);

    }

    @Override
    public void addCover(Album album) {
        albumMapper.insertSelective(album);
    }

    @Override
    public void addImageItem(Integer id, String imageUrl) {
        Album album = findById(id);
        if (album.getImageItems() == null) {
            List<String> imageUrlList = new ArrayList<>();
            imageUrlList.add(imageUrl);
            album.setImageItems(JSONArray.toJSONString(imageUrlList));
        } else {
            JSONArray imageUrlList = JSONArray.parseArray(album.getImageItems());
            imageUrlList.add(imageUrl);
            album.setImageItems(imageUrlList.toJSONString());
        }
        albumMapper.updateByPrimaryKeySelective(album);
    }
}
