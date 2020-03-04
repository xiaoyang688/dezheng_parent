package com.dezheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dezheng.dao.CategoryMapper;
import com.dezheng.dao.SkuMapper;
import com.dezheng.dao.SpuMapper;
import com.dezheng.pojo.goods.Category;
import com.dezheng.pojo.goods.Goods;
import com.dezheng.pojo.goods.Sku;
import com.dezheng.pojo.goods.Spu;
import com.dezheng.service.goods.SpuService;
import com.dezheng.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private IdWorker idWorker;

    @Override
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }

    @Override
    public void saveGoods(Goods goods) {

        //保存Spu
        Spu spu = goods.getSpu();
        spu.setId(idWorker.nextId() + "");
        spuMapper.insert(spu);

        //保存Sku
        List<Sku> skuList = goods.getSkuList();
        for (Sku sku : skuList) {

            Map<String, String> specMap = JSONObject.parseObject(sku.getSpec(), Map.class);

            //循环规格列表
            String spec = "";
            for (String specValue : specMap.values()) {
                spec += specValue + " ";
            }

            sku.setId(idWorker.nextId() + "");
            sku.setBrandName(sku.getBrandName());
            sku.setName(spu.getName() + spec);
            sku.setImage(sku.getImage());
            sku.setImageItems(sku.getImageItems());
            sku.setCreateTime(new Date());
            sku.setUpdateTime(new Date());
            sku.setSpuId(spu.getId());
            sku.setCategory3Id(spu.getCategory3Id());

            //查询分类Id3名称
            Category category = categoryMapper.selectByPrimaryKey(spu.getCategory3Id());
            sku.setCategory3Name(category.getName());
            sku.setSpec(specMap.toString());
            sku.setPrice(sku.getPrice());
            sku.setNum(sku.getNum());
            sku.setAlertNum(sku.getAlertNum());
            skuMapper.insertSelective(sku);
        }
    }
}