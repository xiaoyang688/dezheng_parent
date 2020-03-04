package com.dezheng.service.order;

import com.dezheng.pojo.order.OrderItem;

import java.util.List;

public interface OrderItemService {
    List<OrderItem> findOrderItemsById(String orderId);
}
