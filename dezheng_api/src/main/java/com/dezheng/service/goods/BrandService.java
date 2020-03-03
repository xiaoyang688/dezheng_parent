package com.dezheng.service.goods;

import com.dezheng.entity.PageResult;
import com.dezheng.pojo.goods.Brand;

import java.util.List;

public interface BrandService {

    /**
     * 查询所有品牌
     * @return
     */
    public List<Brand> findAll();

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    public PageResult<Brand> findPage(Integer page, Integer size);

    /**
     * 添加品牌
     * @param brand
     */
    public void add(Brand brand);

    /**
     * 修改品牌
     * @param brand
     */
    public void update(Brand brand);

    /**
     * 删除品牌
     * @param id
     */
    public void delete(Integer id);
}
