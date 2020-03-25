package com.dezheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dezheng.dao.SkuMapper;
import com.dezheng.dao.SpuMapper;
import com.dezheng.pojo.goods.Sku;
import com.dezheng.pojo.goods.Spu;
import com.dezheng.redis.CacheKey;
import com.dezheng.service.goods.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SkuServiceImpl implements SkuService {


    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    public Map findSkuByIdAtRedis(String id) {

        Map item = (Map) redisTemplate.boundHashOps(CacheKey.SkuItem).get(id);

        return item;
    }

    @Override
    public List<Sku> findSkuBySpuIds(String[] spuIds) {

        Example example = new Example(Sku.class);
        example.createCriteria()
                .andIn("spuId", Arrays.asList(spuIds));
        List<Sku> skuList = skuMapper.selectByExample(example);
        if (skuList.size() == 0) {
            throw new RuntimeException("商品不存在");
        }
        return skuList;
    }

    @Override
    public Sku findSkuById(String id) {
        Sku sku = skuMapper.selectByPrimaryKey(id);
        return sku;
    }

    public void saveAllSkuItemToRedis() {
        List<Sku> skuList = skuMapper.selectAll();
        for (Sku sku : skuList) {
            if (sku == null) {
                throw new RuntimeException("该商品不存在！");
            }

            //商品名称
            String name = sku.getName();

            //获取分类id
            Integer category2Id = sku.getCategory2Id();

            //获取价格
            Integer price = sku.getPrice();

            //获取销量
            Integer saleNum = sku.getSaleNum();

            //获取封面图片
            String image = sku.getImage();

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
            item.put("category2Id", category2Id);
            item.put("image", image);
            item.put("imageItems", imageItems);
            item.put("details", details);
            item.put("commentNum", commentNum);

            redisTemplate.boundHashOps(CacheKey.SkuItem).put(sku.getId(), item);
        }
    }

    @Override
    public boolean hasStore(String skuId, Integer num) {
        Sku sku = skuMapper.selectByPrimaryKey(skuId);
        //判断库存是否足够
        if (sku.getNum() > num) {
            return true;
        }
        return false;
    }

    @Override
    public void reduce(String skuId, Integer num) {
        try {
            skuMapper.addSaleNum(skuId, num);
            skuMapper.reduceStoreNum(skuId, num);
        } catch (Exception e) {
            throw new RuntimeException("减少库存失败");
        }
    }
}
