package com.dezheng.controller.cart;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dezheng.entity.Result;
import com.dezheng.service.cart.CartService;
import com.dezheng.service.user.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @Reference
    private UserService userService;

    @GetMapping("/findCartList")
    public List<Map<String, Object>> findCartList(HttpServletRequest request) {
        //获取用户名
        String username = userService.getUserName(request.getHeader("Authorization"));
        return cartService.findCartList(username);
    }

    @GetMapping("/addCart")
    public Result addCart(String skuId, Integer num, HttpServletRequest request) {
        String username = userService.getUserName(request.getHeader("Authorization"));
        cartService.addCart(username, skuId, num);
        return new Result(1, "加购成功");
    }

    @GetMapping("/updateCheckout")
    public Result updateCheckout(String skuId, boolean checkout, HttpServletRequest request) {
        String username = userService.getUserName(request.getHeader("Authorization"));
        cartService.updateCheckout(username, skuId, checkout);
        return new Result(1, "更新成功");
    }

    @GetMapping("/buy")
    public Result buy(String skuId, HttpServletRequest request) {
        String username = userService.getUserName(request.getHeader("Authorization"));
        cartService.buy(username, skuId);
        return new Result(1, "加购成功");
    }

}
