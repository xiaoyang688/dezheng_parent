package com.dezheng.elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

public class RestClientFactory {

    public static RestHighLevelClient getRestHighLevelClient(String hostname, Integer port){

        //连接rest接口
        HttpHost httpHost = new HttpHost(hostname, port, "http");
        //rest构建器
        RestClientBuilder builder = org.elasticsearch.client.RestClient.builder(httpHost);
        //高级客户端对象
        return new RestHighLevelClient(builder);

    }
}
