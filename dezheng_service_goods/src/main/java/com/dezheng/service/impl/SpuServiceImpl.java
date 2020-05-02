package com.dezheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dezheng.dao.BrandMapper;
import com.dezheng.dao.CategoryMapper;
import com.dezheng.dao.SkuMapper;
import com.dezheng.dao.SpuMapper;
import com.dezheng.pojo.goods.*;
import com.dezheng.service.goods.SpuService;
import com.dezheng.utils.IdWorker;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }

    @Override
    public List<Spu> findSpuByIds(String[] ids) {

        Example example = new Example(Spu.class);
        example.createCriteria()
                .andIn("id", Arrays.asList(ids));

        List<Spu> spuList = spuMapper.selectByExample(example);
        if (spuList.size() == 0) {
            throw new RuntimeException("商品不存在");
        }

        return spuList;
    }

    @Override
    public void saveGoods(Goods goods) {

        //保存Spu
        Spu spu = goods.getSpu();
        spu.setId(idWorker.nextId() + "");
        Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
        spu.setName(brand.getName() + " " + spu.getName());
        spu.setIsMarketable("0");
        spu.setIsDelete("0");
        spu.setStatus("0");
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

            //品牌名称
            sku.setBrandName(brand.getName());
            sku.setName(spu.getName() + spec);
            sku.setImage(sku.getImage());
            sku.setImageItems(sku.getImageItems());
            sku.setBusinessMode(brand.getName() + "旗舰店");

            sku.setCreateTime(new Date());
            sku.setUpdateTime(new Date());
            sku.setSpuId(spu.getId());
            sku.setCategory2Id(spu.getCategory2Id());

            //查询分类Id2名称
            Category category = categoryMapper.selectByPrimaryKey(spu.getCategory2Id());
            sku.setCategory2Name(category.getName());
            sku.setBrandName(brand.getName());
            sku.setSpec(JSON.toJSONString(specMap));
            sku.setPrice(sku.getPrice());
            sku.setNum(sku.getNum());
            sku.setAlertNum(sku.getAlertNum());

            sku.setSaleNum(0);
            sku.setCommentNum(0);
            sku.setStatus("1");

            skuMapper.insertSelective(sku);
        }
    }

    @Override
    public void pushOrPull(String[] ids, String type) {
        String ERROR_MESSAGE = "商品不存在或已上架";
        String isMarketable = "0"; //默认查询下架状态
        if (type.equals("0")) {//当下架时，查询上架状态
            isMarketable = "1";
            ERROR_MESSAGE = "商品不存在或已下架";
        }
        //查询符合的Id
        Example example = new Example(Spu.class);
        List<String> idList = Arrays.asList(ids);
        example.createCriteria()
                .andIn("id", idList)
                .andEqualTo("isMarketable", isMarketable);
        List<Spu> spuList = spuMapper.selectByExample(example);

        if (spuList.size() == 0) {
            throw new RuntimeException(ERROR_MESSAGE);
        }

        //更新符合的Spu
        Spu updateSpu = new Spu();
        updateSpu.setIsMarketable(type);
        spuMapper.updateByExampleSelective(updateSpu, example);

        //获取符合的spuId
        List<String> spuIdList = new ArrayList<>();
        for (Spu spu : spuList) {
            spuIdList.add(spu.getId());
        }

        //发送消息更新索引
        if (type.equals("1")) {
            rabbitTemplate.convertAndSend("pullOrPush", "push", JSON.toJSONString(spuIdList));
        } else {
            rabbitTemplate.convertAndSend("pullOrPush", "pull", JSON.toJSONString(spuIdList));
        }
    }


}