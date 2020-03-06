package com.dezheng.service.redis;

import com.dezheng.service.goods.CategoryService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Init implements InitializingBean {

    @Autowired
    private CategoryService categoryService;

    @Override
    public void afterPropertiesSet() throws Exception {

        categoryService.saveCategoryTreeToRedis();
        System.out.println("缓存预热成功！");
    }
}
