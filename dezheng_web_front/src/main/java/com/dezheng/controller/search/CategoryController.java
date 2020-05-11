package com.dezheng.controller.search;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dezheng.service.goods.CategoryService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/category")
public class CategoryController {


    @Reference
    private CategoryService categoryService;

    @GetMapping("/findAllCategory")
    public List<Map> findAllCategory(){
        return categoryService.findAllCategory();
    }

}
