package com.dezheng.controller.goods;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dezheng.entity.Result;
import com.dezheng.pojo.goods.Goods;
import com.dezheng.pojo.goods.Spu;
import com.dezheng.service.goods.SpuService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/spu")
public class SpuController {

    @Reference
    private SpuService spuService;

    @GetMapping("/findAll")
    public Map findAll(){
        List<Spu> spuList = spuService.findAll();
        Map spuListMap = new HashMap();
        spuListMap.put("spuList", spuList);
        return spuListMap;
    }

    @PostMapping("/saveGoods")
    private Result saveGoods(@RequestBody Goods goods){
        spuService.saveGoods(goods);
        return new Result();
    }

}
