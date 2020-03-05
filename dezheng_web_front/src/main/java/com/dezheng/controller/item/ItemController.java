package com.dezheng.controller.item;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dezheng.service.goods.SkuService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/item")
public class ItemController {

    @Reference
    private SkuService skuService;

    @GetMapping("/sku")
    public Map skuItem(@RequestParam("id") String skuId){
        return skuService.findSkuById(skuId);
    }

}
