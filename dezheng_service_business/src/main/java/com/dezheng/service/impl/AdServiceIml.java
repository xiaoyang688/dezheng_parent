package com.dezheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONArray;
import com.dezheng.dao.AdMapper;
import com.dezheng.pojo.business.Ad;
import com.dezheng.service.business.AdService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service
public class AdServiceIml implements AdService {

    @Autowired
    private AdMapper adMapper;

    public Map findByPosition(String position) {

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
        return map;

    }
}
