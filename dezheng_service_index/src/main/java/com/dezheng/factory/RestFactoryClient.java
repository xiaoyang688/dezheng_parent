package com.dezheng.factory;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

public class RestFactoryClient {

    public static RestHighLevelClient getRestHighLevelClient() {
        HttpHost httpHost = new HttpHost("192.168.123.192", 9200, "http");
        RestClientBuilder builder = RestClient.builder(httpHost);
        return new RestHighLevelClient(builder);
    }

}
