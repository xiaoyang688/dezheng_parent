package com.dezheng.dao;

import com.dezheng.pojo.goods.Sku;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

public interface SkuMapper extends Mapper<Sku> {

    /**
     * 减少库存
     * @param skuId
     * @param num
     */
    @Update("UPDATE tb_sku SET num = num - #{num} WHERE id = #{skuId}")
    public void reduceStoreNum(@Param("skuId") String skuId, @Param("num") Integer num);

    /**
     * 增加销量
     * @param skuId
     * @param num
     */
    @Update("UPDATE tb_sku SET sale_num = sale_num + #{num} WHERE id = #{skuId}")
    public void addSaleNum(@Param("skuId") String skuId, @Param("num") Integer num);

}
