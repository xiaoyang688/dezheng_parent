package com.dezheng.service.goods;

import com.dezheng.pojo.goods.Sku;
import com.dezheng.pojo.goods.Spu;

import java.util.List;
import java.util.Map;

public interface SkuService {

    /**
     * 根据ID查询sku
     * @param id
     * @return
     */
    public Map findSkuByIdAtRedis(String id);

    /**
     * 通过spuId查询sku
     * @param spuIds
     * @return
     */
    public List<Sku> findSkuBySpuIds(String[] spuIds);

    /**
     * 通过spuId查询spu
     * @param id
     * @return
     */
    public Sku findSkuById(String id);

    /**
     * 保存所有商品到redis
     */
    public void saveAllSkuItemToRedis();

    /**
     * 是否存在库存
     * @param skuId
     * @param num
     * @return
     */
    public boolean hasStore(String skuId, Integer num);

    /**
     * 减少库存
     * @param skuId
     * @param num
     */
    public void reduce(String skuId, Integer num);

}
