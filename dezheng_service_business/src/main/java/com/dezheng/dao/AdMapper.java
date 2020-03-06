package com.dezheng.dao;

import com.dezheng.pojo.business.Ad;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AdMapper extends Mapper<Ad> {
    /**
     * 查询所有广告位置
     * @return
     */
    @Select("select DISTINCT(position) from tb_ad")
    List<String> findAllPosition();
}
