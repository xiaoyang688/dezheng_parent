package com.dezheng.service.impl.cart;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dezheng.dao.OrderItemMapper;
import com.dezheng.dao.OrderMapper;
import com.dezheng.pojo.order.Order;
import com.dezheng.pojo.order.OrderItem;
import com.dezheng.redis.CacheKey;
import com.dezheng.service.cart.CartService;
import com.dezheng.service.goods.SkuService;
import com.dezheng.utils.IdWorker;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service(interfaceClass = CartService.class)
public class CartServiceImpl implements CartService {


    @Reference
    private SkuService skuService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

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
            orderItem.setCategory2Id(category2Id);
            orderItem.setSkuId(skuId);
            orderItem.setName(name);
            orderItem.setImage(image);
            orderItem.setNum(num);
            orderItem.setPrice(price);
            orderItem.setPayMoney(price * num);

            System.out.println(orderItem);
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

    public List<OrderItem> selectedCartList(String username) {
        List<Map<String, Object>> cartList = findCartList(username);
        List<OrderItem> selectedOrderItem = cartList.stream().filter(cart -> cart.get("checkout").equals(true))
                .map(cart -> (OrderItem) cart.get("orderItem"))
                .collect(Collectors.toList());
        return selectedOrderItem;
    }

    @Override
    @Transactional
    public Map submitOrder(Order order) {

        order.setId(idWorker.nextId() + "");

        //获取勾选购物车
        List<OrderItem> orderItemList = selectedCartList(order.getUsername());
        //插入订单详细
        for (OrderItem orderItem : orderItemList) {
            if (skuService.reduceStore(orderItem.getSkuId(), orderItem.getNum())) {//判断库存是否充足
                orderItem.setOrderId(order.getId());
                orderItem.setId(idWorker.nextId() + "");
                orderItemMapper.insert(orderItem);
            }
        }

        //计算总数量
        IntStream numStream = orderItemList.stream().mapToInt(OrderItem::getNum);
        order.setTotalNum(numStream.sum());

        //计算总金额
        IntStream payMoneyStream = orderItemList.stream().mapToInt(OrderItem::getPayMoney);
        Integer payMoney = payMoneyStream.sum();
        if (payMoney < 4900) {
            payMoney += 0;
        }
        order.setPayMoney(payMoney);

        //创建订单时间
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());

        //状态设置
        order.setOrderStatus("0");
        order.setPayStatus("0");
        order.setConsignStatus("0");
        order.setIsDelete("0");

        //插入订单
        orderMapper.insert(order);

        //删除选中购物车
        delSelectCart(order.getUsername());

        //生成支付连接
        String payUrl = getPayUrl(order.getId(), payMoney);

        //封装订单结果
        Map result = new HashMap();
        result.put("orderId", order.getId());
        result.put("payMoney", order.getPayMoney());
        result.put("payUrl", payUrl);
        result.put("createTime", order.getCreateTime());
        result.put("totalNum", order.getTotalNum());
        return result;
    }

    private String getPayUrl(String orderId, Integer payMoney) {

        OkHttpClient client = new OkHttpClient();
        //格式化小数点
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String format = decimalFormat.format((float) payMoney / (float) 100);
        System.out.println(payMoney);
        System.out.println(format);
        String url = "http://127.0.0.1:10086/qrcode?totalAmount=" + format + "&subject=校园快药收款平台&storeId=123456&timeoutExpress=15m&outTradeNo=" + orderId;

        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            Map responseMap = JSONObject.parseObject(response.body().string(), Map.class);
            String code = (String) responseMap.get("qr_code");
            if (code == null) {
                throw new RuntimeException("生成支付链接失败");
            }
            return code;
        } catch (IOException e) {
            throw new RuntimeException("生成支付链接失败");
        }

    }

    @Override
    public void delSelectCart(String username) {
        List<Map<String, Object>> cartList = findCartList(username);
        if (cartList == null) {
            throw new RuntimeException("购物车为空");
        }
        //筛选未选中的购物车
        List<Map<String, Object>> falseCart = cartList.stream().filter(cart -> cart.get("checkout").equals(false)).collect(Collectors.toList());
        redisTemplate.boundHashOps(CacheKey.CartList).put(username, falseCart);
    }

}
