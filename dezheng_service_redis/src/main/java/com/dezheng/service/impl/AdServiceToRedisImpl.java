package com.dezheng.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.dezheng.service.business.AdService;
import com.dezheng.service.redis.AdServiceToRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

@Service
public class AdServiceToRedisImpl implements AdServiceToRedis {

    @Autowired
    private RedisTemplate redisTemplate;

    @Reference
    private AdService adService;

    @Override
    public void saveAllAdToRedis() {

    }
}
