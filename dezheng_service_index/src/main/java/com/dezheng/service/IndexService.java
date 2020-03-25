package com.dezheng.service;

public interface IndexService {

    /**
     * 添加索引
     */
    public String addIndex(String[] spuIds);

    /**
     * 删除索引
     * @param spuIds
     */
    public String delIndex(String[] spuIds);

}
