package com.dezheng.service.impl.order;

import com.alibaba.dubbo.config.annotation.Service;
import com.dezheng.dao.OrderItemMapper;
import com.dezheng.dao.OrderMapper;
import com.dezheng.pojo.order.Order;
import com.dezheng.pojo.order.OrderCompose;
import com.dezheng.pojo.order.OrderItem;
import com.dezheng.service.order.OrderItemService;
import com.dezheng.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderItemService orderItemService;

    public OrderCompose findOrderComposeById(String orderId) {

        OrderCompose orderCompose = new OrderCompose();
        //查询order
        Order order = orderMapper.selectByPrimaryKey(orderId);

        //查询orderItem
        List<OrderItem> orderItemList = orderItemService.findOrderItemsById(orderId);

        //封装实体
        orderCompose.setOrder(order);
        orderCompose.setOrderItemList(orderItemList);
        return orderCompose;
    }
}
