package com.dezheng.service.impl.cart;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.dezheng.pojo.goods.Sku;
import com.dezheng.pojo.order.OrderItem;
import com.dezheng.redis.CacheKey;
import com.dezheng.service.cart.CartService;
import com.dezheng.service.goods.SkuService;
import com.dezheng.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {


    @Reference
    private SkuService skuService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Override
    public List<Map<String, Object>> findCartList(String username) {
        List<Map<String, Object>> cartList = (List<Map<String, Object>>) redisTemplate.boundHashOps(CacheKey.CartList).get(username);
        if (cartList == null) {
            return new ArrayList<>();
        }
        return cartList;
    }

    @Override
    public List<Map<String, Object>> findNewCartList(String username) {
        List<Map<String, Object>> cartList = findCartList(username);
        for (Map<String, Object> cart : cartList) {
            OrderItem orderItem = (OrderItem) cart.get("orderItem");
            Sku sku = skuService.findSkuById(orderItem.getSkuId());
            orderItem.setPrice(sku.getPrice());
            orderItem.setPayMoney(orderItem.getPrice() * orderItem.getNum());
        }
        redisTemplate.boundHashOps(CacheKey.CartList).put(username, cartList);
        return cartList;
    }

    @Override
    public void addCart(String username, String skuId, Integer num) {

        //获取sku商品信息
        Map skuMap = skuService.findSkuByIdAtRedis(skuId);

        if (skuMap == null) {
            throw new RuntimeException("商品不存在");
        }

        Integer price = (Integer) skuMap.get("price");
        String name = (String) skuMap.get("name");
        String image = (String) skuMap.get("image");
        Integer category2Id = (Integer) skuMap.get("category2Id");

        //查找购物车
        List<Map<String, Object>> cartList = findCartList(username);

        boolean isAddCart = false;

        if (cartList.size() > 0) {
            //遍历购物车
            for (Map<String, Object> cart : cartList) {
                //获取商品信息
                OrderItem orderItem = (OrderItem) cart.get("orderItem");
                //查找是否有存在的商品
                if (orderItem.getSkuId().equals(skuId)) { //存在商品
                    isAddCart = true;
                    if (orderItem.getNum() > 0) {
                        orderItem.setNum(orderItem.getNum() + num);
                        orderItem.setPayMoney(price * orderItem.getNum());
                    }
                    if (orderItem.getNum() <= 0) {
                        cartList.remove(cart);
                        break;
                    }
                }
            }
        }

        if (isAddCart == false) {
            //商品不存在购物车,并添加购物车
            if (num <= 0) {
                throw new RuntimeException("数量不合法");
            }

            Map<String, Object> cartMap = new HashMap<>();
            OrderItem orderItem = new OrderItem();
            orderItem.setId(idWorker.nextId() + "");
            orderItem.setCategory2Id(category2Id);
            orderItem.setSkuId(skuId);
            orderItem.setName(name);
            orderItem.setImage(image);
            orderItem.setNum(num);
            orderItem.setPrice(price);
            orderItem.setPayMoney(price * num);

            //默认勾选
            cartMap.put("orderItem", orderItem);
            cartMap.put("checkout", true);
            cartList.add(cartMap);
        }
        //重新保存到redis中
        redisTemplate.boundHashOps(CacheKey.CartList).put(username, cartList);
    }

    @Override
    public void updateCheckout(String username, String skuId, boolean checkout) {
        //查找购物车
        List<Map<String, Object>> cartList = findCartList(username);

        //更新购物车
        if (cartList.size() > 0) {
            for (Map<String, Object> cart : cartList) {
                OrderItem orderItem = (OrderItem) cart.get("orderItem");
                if (orderItem.getSkuId().equals(skuId)) {
                    cart.put("checkout", checkout);
                }
            }
        }
        redisTemplate.boundHashOps(CacheKey.CartList).put(username, cartList);
    }

    @Override
    public void buy(String username, String skuId) {
        addCart(username, skuId, 1);
    }
}
