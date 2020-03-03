package com.dezheng.controller.goods;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dezheng.entity.PageResult;
import com.dezheng.entity.Result;
import com.dezheng.pojo.goods.Brand;
import com.dezheng.service.goods.BrandService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    @GetMapping("/findAll")
    public List<Brand> findAll() {
        int i=1/0;
        return brandService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Brand> findPage(Integer page, Integer size) {
        return brandService.findPage(page, size);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Brand brand) {
        brandService.add(brand);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Brand brand) {
        brandService.update(brand);
        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(Integer id){
        brandService.delete(id);
        return new Result();
    }
}
