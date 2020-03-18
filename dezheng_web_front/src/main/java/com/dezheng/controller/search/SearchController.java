package com.dezheng.controller.search;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dezheng.service.search.SkuSearchService;
import com.dezheng.utils.WebUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SearchController {

    @Reference
    private SkuSearchService skuSearchService;

    @GetMapping("/search")
    public Map search(@RequestParam Map<String, String> searchMap){
        try {
            System.out.println(searchMap);
            searchMap = WebUtil.convertCharsetToUTF8(searchMap);
            System.out.println(searchMap);
            return skuSearchService.search(searchMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
