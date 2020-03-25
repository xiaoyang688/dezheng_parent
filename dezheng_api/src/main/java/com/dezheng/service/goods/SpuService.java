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
     * 通过id查询spu
     * @param ids
     * @return
     */
    public List<Spu> findSpuByIds(String[] ids);

    /**
     * 保存商品
     * @param goods
     */
    public void saveGoods(Goods goods);


    /**
     * 上架或下架商品
     * @param ids
     * @param type
     */
    public void pushOrPull(String[] ids, String type);
}
