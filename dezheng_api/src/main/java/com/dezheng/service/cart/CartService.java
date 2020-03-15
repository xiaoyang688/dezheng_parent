package com.dezheng.service.cart;

import java.util.List;
import java.util.Map;

public interface CartService {

    /**
     * 查找购物车
     * @return
     */
    public List<Map<String, Object>> findCartList(String username);

    /**
     * 更新购物车价格
     * @param username
     * @return
     */
    public List<Map<String, Object>> findNewCartList(String username);



    /**
     * 添加购物车
     * @param username
     * @param skuId
     * @param num
     */
    public void addCart(String username, String skuId, Integer num);

    /**
     * 更新勾选框
     * @param username
     * @param skuId
     * @param checkout
     */
    public void updateCheckout(String username, String skuId, boolean checkout);

    /**
     * 详细页加购
     * @param username
     * @param skuId
     */
    public void buy(String username, String skuId);

}
