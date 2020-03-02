package com.dezheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dezheng.dao.SpuMapper;
import com.dezheng.pojo.goods.Spu;
import com.dezheng.service.goods.SpuService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuMapper spuMapper;

    @Override
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }

}