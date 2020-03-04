package com.dezheng.service.goods;

import com.dezheng.pojo.goods.Goods;
import com.dezheng.pojo.goods.Spu;

import java.util.List;

public interface SpuService {
    /**
     * 查询所有Spu
     * @return
     */
    public List<Spu> findAll();

    /**
     * 保存商品
     * @param goods
     */
    public void saveGoods(Goods goods);
}
