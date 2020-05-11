package com.dezheng.controller.item;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dezheng.service.goods.SkuService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/item")
public class ItemController {

    @Reference
    private SkuService skuService;

    @GetMapping("/sku")
    public Map skuItem(@RequestParam("id") String skuId){
        return skuService.findSkuByIdAtRedis(skuId);
    }

}
