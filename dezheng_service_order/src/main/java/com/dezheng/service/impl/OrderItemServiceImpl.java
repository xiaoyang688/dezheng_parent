package com.dezheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dezheng.dao.OrderItemMapper;
import com.dezheng.pojo.order.OrderItem;
import com.dezheng.service.order.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public List<OrderItem> findOrderItemsById(String orderId) {

        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(orderId);

        List<OrderItem> orderItemList = orderItemMapper.select(orderItem);
        return orderItemList;
    }
}
