package com.dezheng.service.cart;

import java.util.List;
import java.util.Map;

public interface CartService {

    /**
     * 查找购物车
     * @return
     */
    public List<Map<String, Object>> findCartList(String username);

    public void addCart(String username, String skuId, Integer num);

}
