package com.dezheng.service.goods;

import com.dezheng.pojo.goods.Album;

import java.util.List;

public interface AlbumService {

    public Album findById(Integer id);

    /**
     * 添加商品封面
     * @param album
     */
    public void addCover(Album album);

    /**
     * 添加图片集
     * @param
     */
    public void addImageItem(Integer id, String imageUrl);

}
