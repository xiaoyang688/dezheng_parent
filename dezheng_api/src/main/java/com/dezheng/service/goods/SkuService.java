package com.dezheng.service.goods;

import com.dezheng.pojo.goods.Sku;

import java.util.Map;

public interface SkuService {

    /**
     * 根据ID查询sku
     * @param id
     * @return
     */
    public Map findSkuByIdAtRedis(String id);

    public Sku findSkuById(String id);

    /**
     * 保存所有商品到redis
     */
    public void saveAllSkuItemToRedis();

    /**
     * 减少库存
     * @param skuId
     * @param num
     * @return
     */
    public boolean reduceStore(String skuId, Integer num);
}
