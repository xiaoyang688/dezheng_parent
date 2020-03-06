package com.dezheng.index;


import com.dezheng.dao.SkuMapper;
import com.dezheng.pojo.goods.Sku;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportEs {

    @Autowired
    private SkuMapper skuMapper;

    @Test
    public void testMapper(){
        List<Sku> skus = skuMapper.selectAll();
        for (Sku sku : skus) {
            System.out.println(sku.getName());
        }
    }

    @Test
    public void importEs(){
        HttpHost httpHost = new HttpHost("192.168.123.192", 9200, "http");
        RestClientBuilder builder = RestClient.builder(httpHost);
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(builder);

        BulkRequest bulkRequest = new BulkRequest();
        IndexRequest indexRequest = new IndexRequest("sku", "doc", "123");

        Map skuMap = new HashMap();
        skuMap.put("name", "test1");
        skuMap.put("randName", "牛逼");
        skuMap.put("price", "3000");
        indexRequest.source(skuMap);
        bulkRequest.add(indexRequest);

        BulkResponse responses = null;
        try {
            responses = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int status = responses.status().getStatus();
        String s = responses.buildFailureMessage();
        System.out.println(status);
        System.out.println(s);

    }

}
