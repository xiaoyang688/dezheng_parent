package com.dezheng.service.business;

import java.util.Map;

public interface AdService {
    /**
     * 根据位置查找广告
     * @param position
     * @return
     */
    Map findByPosition(String position);

    /**
     * 测试缓存
     */
    public void testRedis();
}
