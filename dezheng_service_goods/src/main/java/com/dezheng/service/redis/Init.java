package com.dezheng.service.redis;

import com.dezheng.service.goods.CategoryService;
import com.dezheng.service.goods.SkuService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Init implements InitializingBean {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SkuService skuService;

    @Override
    public void afterPropertiesSet() throws Exception {
        //保存所有分类
        categoryService.saveCategoryTreeToRedis();

        //保存所有商品详细数据
        skuService.saveAllSkuItemToRedis();

        System.out.println("缓存预热成功！");
    }
}
