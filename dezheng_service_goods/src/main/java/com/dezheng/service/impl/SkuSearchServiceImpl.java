package com.dezheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dezheng.service.search.SkuSearchService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class SkuSearchServiceImpl implements SkuSearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public Map search(Map<String, String> searchMap) {

        SearchRequest searchRequest = new SearchRequest("sku");
        searchRequest.types("item");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //分页
        Integer page = Integer.parseInt(searchMap.get("page"));
        Integer size = Integer.parseInt(searchMap.get("size"));

        Integer offset = (page - 1) * size;
        searchSourceBuilder.from(offset);
        searchSourceBuilder.size(size);

        //布尔查询构造器
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //关键字查询
        String keyword = searchMap.get("keyword");

        if (keyword != null) {
            MatchQueryBuilder nameQueryBuilder = QueryBuilders.matchQuery("name", keyword);
            MatchQueryBuilder categoryQueryBuilder = QueryBuilders.matchQuery("categoryName", keyword);
            boolQueryBuilder.should(nameQueryBuilder);
            boolQueryBuilder.should(categoryQueryBuilder);
        }

        //分类查询
        String category = searchMap.get("category");
        if (category != null) {
            MatchQueryBuilder categoryNameQueryBuilder = QueryBuilders.matchQuery("categoryName", category);
            boolQueryBuilder.must(categoryNameQueryBuilder);
        }


        searchSourceBuilder.query(boolQueryBuilder);

        searchRequest.source(searchSourceBuilder);

        SearchResponse response = null;
        try {

            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            //封装结果集
            Map resultMap = new HashMap();
            List<Map<String, Object>> resultList = new ArrayList<>();
            for (SearchHit hit : hits) {
                Map<String, Object> hitSourceAsMap = hit.getSourceAsMap();
                resultList.add(hitSourceAsMap);
            }

            long totalHits = hits.getTotalHits();
            resultMap.put("rows", resultList);
            resultMap.put("totalCount", totalHits);

            return resultMap;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
