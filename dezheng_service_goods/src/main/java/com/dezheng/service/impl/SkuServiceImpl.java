package com.dezheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dezheng.dao.SkuMapper;
import com.dezheng.dao.SpuMapper;
import com.dezheng.pojo.goods.Sku;
import com.dezheng.pojo.goods.Spu;
import com.dezheng.service.goods.SkuService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

@Service
public class SkuServiceImpl implements SkuService {


    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private SpuMapper spuMapper;

    @Override
    public Map findSkuById(String id) {

        Sku sku = skuMapper.selectByPrimaryKey(id);
        if (sku == null) {
            throw new RuntimeException("该商品不存在！");
        }

        //商品名称
        String name = sku.getName();

        //获取价格
        Integer price = sku.getPrice();

        //获取销量
        Integer saleNum = sku.getSaleNum();

        //获取图片
        String[] imageItems = sku.getImageItems().split(",");

        //说明书
        String spuId = sku.getSpuId();
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        String introduction = spu.getIntroduction();

        //获取规格
        String spec = sku.getSpec();

        //详情图片
        String details = spu.getDetails();

        //获取评论数
        Integer commentNum = sku.getCommentNum();

        Map item = new HashMap();
        item.put("name", name);
        item.put("price", price);
        item.put("saleNum", saleNum);
        item.put("introduction", introduction);
        item.put("spec", spec);
        item.put("imageItems", imageItems);
        item.put("details", details);
        item.put("commentNum", commentNum);

        return item;
    }
}
