package com.dezheng.service.order;

import com.dezheng.pojo.order.OrderCompose;

import java.util.List;

public interface OrderService {

    /**
     * 通过ID查询订单实体
     *
     * @param orderId
     * @return
     */
    OrderCompose findOrderComposeById(String orderId);

}
