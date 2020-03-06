package com.dezheng.service.goods;

import java.util.List;
import java.util.Map;

public interface CategoryService {

    //查找全部分类
    public List<Map> findAllCategory();

    //保存CategoryTree到redis中
    public void saveCategoryTreeToRedis();

}
