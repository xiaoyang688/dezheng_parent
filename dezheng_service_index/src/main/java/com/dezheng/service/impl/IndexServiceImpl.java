package com.dezheng.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.dezheng.pojo.goods.Sku;
import com.dezheng.service.IndexService;
import com.dezheng.service.goods.SkuService;
import com.dezheng.service.goods.SpuService;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class IndexServiceImpl implements IndexService {

    @Reference
    private SpuService spuService;

    @Reference
    private SkuService skuService;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public String addIndex(String[] spuIds) {
        //通过spuId查询spu
        List<Sku> skuList = skuService.findSkuBySpuIds(spuIds);
        if (skuList.size() == 0) {
            return "fail";
        }

        BulkRequest bulkRequest = new BulkRequest();
        //添加搜索索引
        for (Sku sku : skuList) {
            IndexRequest indexRequest = new IndexRequest("sku", "item", sku.getId());
            Map skuMap = new HashMap();
            skuMap.put("id", sku.getId());
            skuMap.put("name", sku.getName());
            skuMap.put("image", sku.getImage());
            skuMap.put("price", sku.getPrice());
            skuMap.put("saleNum", sku.getSaleNum());
            skuMap.put("commentNum", sku.getCommentNum());
            skuMap.put("businessMode", sku.getBusinessMode());
            skuMap.put("brandName", sku.getBrandName());
            skuMap.put("categoryName", sku.getCategory2Name());
            indexRequest.source(skuMap);
            bulkRequest.add(indexRequest);
        }

        try {
            BulkResponse response = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            System.out.println(response.buildFailureMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "success";
    }

    @Override
    public String delIndex(String[] spuIds) {

        //通过spuId查询spu
        List<Sku> skuList = skuService.findSkuBySpuIds(spuIds);
        if (skuList.size() == 0) {
            return "fail";
        }

        //删除索引
        BulkRequest bulkRequest = new BulkRequest();
        for (Sku sku : skuList) {
            DeleteRequest deleteRequest = new DeleteRequest("sku", "item", sku.getId());
            bulkRequest.add(deleteRequest);
        }

        try {
            BulkResponse response = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            System.out.println(response.buildFailureMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "success";
    }
}
