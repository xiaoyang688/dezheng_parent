package com.dezheng.service.goods;

import java.util.Map;

public interface SkuService {

    /**
     * 根据ID查询sku
     * @param id
     * @return
     */
    public Map findSkuById(String id);

    /**
     * 保存所有商品到redis
     */
    public void saveAllSkuItemToRedis();
}
