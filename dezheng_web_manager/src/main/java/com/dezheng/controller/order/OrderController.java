package com.dezheng.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dezheng.pojo.order.OrderCompose;
import com.dezheng.service.order.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;

    @GetMapping("/findOrderById")
    public OrderCompose findOrderById(String id){
        return orderService.findOrderComposeById(id);
    }

}
