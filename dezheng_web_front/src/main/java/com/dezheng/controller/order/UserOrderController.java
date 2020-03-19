package com.dezheng.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dezheng.entity.Result;
import com.dezheng.pojo.order.Order;
import com.dezheng.pojo.order.OrderCompose;
import com.dezheng.service.order.OrderService;
import com.dezheng.service.user.UserService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class UserOrderController {

    @Reference
    private OrderService orderService;

    @Reference
    private UserService userService;

    @GetMapping("/findOrderById")
    public Map findOrderById(HttpServletRequest request, String id) {
        String username = userService.getUserName(request.getHeader("Authorization"));
        return orderService.findOrderComposeById(username, id);
    }

    @PostMapping("/submitOrder")
    public Map submitOrder(@RequestBody Order order, HttpServletRequest request) {
        String username = userService.getUserName(request.getHeader("Authorization"));
        order.setUsername(username);
        Map result = orderService.submitOrder(order);
        return result;
    }

    @GetMapping("/deleteOrderById")
    public Result deleteOrderById(HttpServletRequest request, String id){
        String username = userService.getUserName(request.getHeader("Authorization"));
        orderService.deleteOrderById(username, id);
        return new Result(1, "删除成功");
    }

    @GetMapping("/findOrderByStatus")
    public List<OrderCompose> findOrderByStatus(HttpServletRequest request, String status){
        String username = userService.getUserName(request.getHeader("Authorization"));
        return orderService.findOrderByStatus(username, status);
    }

}
