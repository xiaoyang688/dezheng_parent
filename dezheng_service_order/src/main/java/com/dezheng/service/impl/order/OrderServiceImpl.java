package com.dezheng.service.impl.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dezheng.dao.OrderItemMapper;
import com.dezheng.dao.OrderMapper;
import com.dezheng.pojo.goods.Sku;
import com.dezheng.pojo.order.Order;
import com.dezheng.pojo.order.OrderCompose;
import com.dezheng.pojo.order.OrderItem;
import com.dezheng.service.cart.CartService;
import com.dezheng.service.goods.SkuService;
import com.dezheng.service.order.OrderItemService;
import com.dezheng.service.order.OrderService;
import com.dezheng.utils.IdWorker;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.IntStream;

@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl implements OrderService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private CartService cartService;

    @Reference
    private SkuService skuService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitSentFront;

    public Map findOrderComposeById(String username, String orderId) {

        //查询order
        Example example = new Example(Order.class);
        example.createCriteria()
                .andEqualTo("id", orderId)
                .andEqualTo("username", username)
                .andEqualTo("isDelete", "0");
        List<Order> orderList = orderMapper.selectByExample(example);
        if (orderList.size() == 0) {
            throw new RuntimeException("订单不存在");
        }
        //获取订单
        Order order = orderList.get(0);

        //查询orderItem
        List<OrderItem> orderItemList = orderItemService.findOrderItemsById(orderId);

        //封装实体
        Map orderCompose = new HashMap();
        orderCompose.put("order", order);
        orderCompose.put("goods", orderItemList);
        return orderCompose;
    }

    @Transactional
    public Map submitOrder(Order order) {

        System.out.println("我被调用了");

        order.setId(idWorker.nextId() + "");

        //获取勾选购物车
        List<OrderItem> orderItemList = cartService.selectedCartList(order.getUsername());

        if (orderItemList.size() == 0) {
            throw new RuntimeException("购物车不能为空");
        }

        //插入订单详细
        for (OrderItem orderItem : orderItemList) {
            if (skuService.hasStore(orderItem.getSkuId(), orderItem.getNum())) {//判断库存是否充足
                //减少库存
                skuService.reduce(orderItem.getSkuId(), orderItem.getNum());
                //生成订单详细
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
        Integer totalMoney = payMoney;
        if (payMoney < 4900) {
            totalMoney = payMoney + 0;
        }
        order.setPayMoney(payMoney);
        order.setTotalMoney(totalMoney);

        //创建订单时间
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());

        //结束订单
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 15);
        order.setEndTime(calendar.getTime());

        //状态设置
        order.setOrderStatus("1");
        order.setPayStatus("0");
        order.setConsignStatus("0");
        order.setIsDelete("0");

        //生成支付连接
        String payUrl = getPayUrl(order.getId(), payMoney);
        order.setPayUrl(payUrl);

        //插入订单
        orderMapper.insert(order);

        //删除选中购物车
        cartService.delSelectCart(order.getUsername());

        Map result = new HashMap();
        result.put("orderId", order.getId());
        return result;
    }

    public String updateOrderStatus(String orderId, String transactionId) {

        Order order = orderMapper.selectByPrimaryKey(orderId);

        if (order == null) {
            return "订单不存在";
        }

        if (order.getPayStatus().equals("1")) {
            return "订单已支付";
        }


        order.setOrderStatus("2");
        order.setPayStatus("1");
        order.setTransactionId(transactionId);
        order.setPayTime(new Date());
        orderMapper.updateByPrimaryKeySelective(order);

        //发送消息给前端
        rabbitSentFront.convertAndSend("paynotify", "", JSON.toJSONString(order));
        return "更新成功";
    }

    public void deleteOrderById(String username, String id) {

        Example example = new Example(Order.class);
        example.createCriteria()
                .andEqualTo("id", id)
                .andEqualTo("username", username);

        List<Order> orders = orderMapper.selectByExample(example);
        if (orders.size() == 0) {
            throw new RuntimeException("订单不存在");
        }
        Order searchOrder = orders.get(0);

        if (searchOrder.getIsDelete().equals("1")) {
            throw new RuntimeException("订单已删除");
        }

        if (searchOrder == null) {
            throw new RuntimeException("订单不存在");
        }
        searchOrder.setIsDelete("1");
        orderMapper.updateByPrimaryKey(searchOrder);

        //增加库存，减少销量
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(searchOrder.getId());
        List<OrderItem> orderItemList = orderItemMapper.select(orderItem);
        for (OrderItem item : orderItemList) {
            Sku sku = skuService.findSkuById(item.getSkuId());
            skuService.reduce(sku.getId(), -item.getNum());
        }

        System.out.println("程序执行到这！");
    }

    public List<OrderCompose> findOrderByStatus(String username, String status) {

        List<OrderCompose> orderComposeList = new ArrayList<>();

        Example example = new Example(Order.class);
        if (status != null) {
            example.createCriteria()
                    .andEqualTo("username", username)
                    .andEqualTo("orderStatus", status)
                    .andEqualTo("isDelete", "0");
        } else {
            example.createCriteria()
                    .andEqualTo("username", username)
                    .andEqualTo("isDelete", "0");
        }


        List<Order> orders = orderMapper.selectByExample(example);

        if (orders.size() == 0) {
            throw new RuntimeException("订单不存在");
        }

        //封装订单组合
        for (Order order : orders) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            List<OrderItem> orderItemList = orderItemMapper.select(orderItem);
            OrderCompose orderCompose = new OrderCompose();
            orderCompose.setOrder(order);
            orderCompose.setOrderItemList(orderItemList);
            orderComposeList.add(orderCompose);
        }

        return orderComposeList;
    }

    private String getPayUrl(String orderId, Integer payMoney) {

        System.out.println("======>" + orderId);

        OkHttpClient client = new OkHttpClient();
        //格式化小数点
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String format = decimalFormat.format((float) payMoney / (float) 100);
        if (format.equals(0)) {
            throw new RuntimeException("生成订单出现错误");
        }
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
}
