package com.dezheng.service.goods;

import java.util.Map;

public interface SkuService {

    /**
     * 通过id查找sku
     * @param id
     * @return
     */
    public Map findSkuById(String id);

    public void saveAllSkuItemToRedis();

}
