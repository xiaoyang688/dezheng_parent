package com.dezheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dezheng.dao.CategoryMapper;
import com.dezheng.pojo.goods.Category;
import com.dezheng.redis.CacheKey;
import com.dezheng.service.goods.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Map> findAllCategory() {
        List<Map> categoryTree = (List<Map>) redisTemplate.boundHashOps(CacheKey.CategoryTree).get("categoryTree");
        if (categoryTree == null) {
            saveCategoryTreeToRedis();
        }
        return categoryTree;
    }

    public void saveCategoryTreeToRedis() {

        Example example = new Example(Category.class);
        example.createCriteria()
                .andEqualTo("isShow", "1")
                .andEqualTo("isMenu", "1");

        List<Category> categoryList = categoryMapper.selectByExample(example);

        List<Map> categoryTree = findCategoryByParent(categoryList, 0);

        redisTemplate.boundHashOps(CacheKey.CategoryTree).put("categoryTree", categoryTree);

    }


    private List<Map> findCategoryByParent(List<Category> categoryList, Integer parentId) {

        List<Map> categoryTree = new ArrayList<>();
        for (Category category : categoryList) {
            if (category.getParentId() == parentId) {
                Map map = new HashMap<>();
                map.put("id", category.getId());
                map.put("name", category.getName());
                List<Map> menu = findCategoryByParent(categoryList, category.getId());
                if (menu.size() > 0) {
                    map.put("menu", menu);
                }
                categoryTree.add(map);
            }
        }
        return categoryTree;

    }
}
