package com.dezheng.service.order;

import com.dezheng.pojo.order.Order;
import com.dezheng.pojo.order.OrderCompose;

import java.util.List;
import java.util.Map;

public interface OrderService {

    /**
     *  根据订单id查询订单
     * @param username
     * @param orderId
     * @return
     */
    public Map findOrderComposeById(String username, String orderId);

    /**
     * 提交订单
     * @param order
     * @return
     */
    public Map submitOrder(Order order);

    /**
     * 更新订单状态
     * @param orderId
     */
    public String updateOrderStatus(String orderId, String transactionId);

    /**
     * 删除订单
     * @param id
     */
    public void deleteOrderById(String username, String id);

    public List<OrderCompose> findOrderByStatus(String username, String status);

}
