package com.dezheng.controller.search;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dezheng.service.search.SkuSearchService;
import com.dezheng.utils.WebUtil;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class SearchController {

    @Reference
    private SkuSearchService skuSearchService;

    @GetMapping("/search")
    public Map search(@RequestParam String page,
                      @RequestParam String size,
                      @RequestParam(required = false) String keyword,
                      @RequestParam(required = false) String category) throws UnsupportedEncodingException {
        if (keyword != null) {
            keyword = new String(keyword.getBytes("ISO8859-1"), "UTF-8");
        } else if (category != null) {
            category = new String(category.getBytes("ISO8859-1"), "UTF-8");
        }
        Map<String, String> searchMap = new HashMap<>(16);
        searchMap.put("page", page);
        searchMap.put("size", size);
        searchMap.put("keyword", keyword);
        searchMap.put("category", category);
        try {
            System.out.println(searchMap);
            return skuSearchService.search(searchMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
