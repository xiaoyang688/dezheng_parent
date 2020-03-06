package com.dezheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dezheng.dao.AdMapper;
import com.dezheng.pojo.business.Ad;
import com.dezheng.redis.CacheKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.*;


@Service
public class AdServiceImpl implements com.dezheng.service.business.AdService {

    @Autowired
    private AdMapper adMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    public Map findByPosition(String position) {
        return  (Map) redisTemplate.boundHashOps(CacheKey.AD).get(position);
    }

    @Override
    public void saveAdByPositionToRedis() {

        List<String> positionList = adMapper.findAllPosition();
        for (String position : positionList) {
            //查询符合条件的广告
            Example example = new Example(Ad.class);
            example.createCriteria()
                    .andEqualTo("position", position)
                    .andGreaterThanOrEqualTo("endTime", new Date())
                    .andLessThanOrEqualTo("startTime", new Date())
                    .andEqualTo("status", "1");
            List<Ad> adList = adMapper.selectByExample(example);
            Map map = new HashMap();
            for (Ad ad : adList) {
                List<String> result = new ArrayList<>();
                result.add(ad.getImage());
                result.add(ad.getUrl());
                map.put(ad.getName(), result);
            }
            //保存到redis
            redisTemplate.boundHashOps(CacheKey.AD).put(position, map);
        }
    }
}
