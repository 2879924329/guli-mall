package com.wch.gulimall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wch.common.constant.OrderConstant;
import com.wch.common.to.MemberEntityTo;
import com.wch.common.utils.PageUtils;
import com.wch.common.utils.Query;
import com.wch.common.utils.R;
import com.wch.gulimall.order.dao.OrderDao;
import com.wch.gulimall.order.entity.OrderEntity;
import com.wch.gulimall.order.entity.OrderItemEntity;
import com.wch.gulimall.order.enume.OrderStatusEnum;
import com.wch.gulimall.order.feign.CartFeignService;
import com.wch.gulimall.order.feign.MemberFeignService;
import com.wch.gulimall.order.feign.ProductFeignService;
import com.wch.gulimall.order.feign.WaresFeignService;
import com.wch.gulimall.order.interceptor.LoginUserInterceptor;
import com.wch.gulimall.order.service.OrderItemService;
import com.wch.gulimall.order.service.OrderService;
import com.wch.gulimall.order.to.MemberAddressTo;
import com.wch.gulimall.order.to.OrderCreateTo;
import com.wch.gulimall.order.vo.OrderItemVo;
import com.wch.gulimall.order.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {


    private final ThreadLocal<OrderSubmitVo> orderSubmitVoThreadLocal = new ThreadLocal<>();

    @Autowired
    private MemberFeignService memberFeignService;

    @Autowired
    private CartFeignService cartFeignService;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private WaresFeignService waresFeignService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderItemService orderItemService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    /**
     * 订单确认页返回需要用的数据
     *
     * @return orderConfirmVo
     */
    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        MemberEntityTo memberEntityTo = LoginUserInterceptor.loginUser.get();
        Long id = memberEntityTo.getId();
        /**
         * 获取之前的请求数据据，异步编程共享ThreadLocal的数据
         */
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        //远程查询收获列表
        CompletableFuture<Void> addressFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<MemberAddressTo> address = memberFeignService.getAddress(id);
            orderConfirmVo.setAddress(address);
        }, threadPoolExecutor);
        //远程查询购物车购物项数据， feign远程调用之前要构造请求，调用很多的拦截器
        CompletableFuture<Void> cartItemsFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<OrderItemVo> cartItems = cartFeignService.getCartItems();
            orderConfirmVo.setItems(cartItems);
        }, threadPoolExecutor).thenRunAsync(() -> {
            //异步去库存服务查询库存
            List<OrderItemVo> items = orderConfirmVo.getItems();
            List<Long> collect = items.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
            R skuStock = waresFeignService.getSkuStock(collect);
            List<SkuHasStockVo> hasStockVos = skuStock.getData(new TypeReference<List<SkuHasStockVo>>() {
            });
            if (!CollectionUtils.isEmpty(hasStockVos)) {
                Map<Long, Boolean> stockMap = hasStockVos.stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
                orderConfirmVo.setStocks(stockMap);
            }
        }, threadPoolExecutor);
        //查询用户积分
        Integer integration = memberEntityTo.getIntegration();
        orderConfirmVo.setIntegration(integration);

        //防重令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        stringRedisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberEntityTo.getId(), token, 30, TimeUnit.MINUTES);
        orderConfirmVo.setOrderToken(token);
        CompletableFuture.allOf(addressFuture, cartItemsFuture).get();
        return orderConfirmVo;
    }

    /**
     * 提交订单
     *
     * @param orderSubmitVo
     * @return
     *
     * 为了保证高并发，
     */
    @Transactional
    @Override
    public OrderSubmitResponseVo submitOrder(OrderSubmitVo orderSubmitVo) {
        orderSubmitVoThreadLocal.set(orderSubmitVo);
        OrderSubmitResponseVo orderSubmitResponseVo = new OrderSubmitResponseVo();
        orderSubmitResponseVo.setCode(0);
        MemberEntityTo memberEntityTo = LoginUserInterceptor.loginUser.get();
        //创建订单，验证令牌，验证价格，锁库存
        //验证令牌,对比和删除令牌的操作必须保证原子性
        String orderToken = orderSubmitVo.getOrderToken();
 /*       String redisOrderToken = stringRedisTemplate.opsForValue().get(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberEntityTo.getId());
        if (!ObjectUtils.isEmpty(redisOrderToken) && orderToken.equals(redisOrderToken)){

            //删除令牌

        }else {
            //验证令牌不通过
            return null;
        }*/
        String key = OrderConstant.USER_ORDER_TOKEN_PREFIX + memberEntityTo.getId();
        // 0:令牌校验失败 1：令牌校验成功
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        //原子验证令牌和删除令牌
        Long result = stringRedisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Collections.singletonList(key), orderToken);
        if (result == 0) {
            //验证令牌不通过
            orderSubmitResponseVo.setCode(1);
            return orderSubmitResponseVo;
        } else {
            //创建订单，验证价格，锁库存
            OrderCreateTo order = createOrder();
            //数据库查询出来的价格
            BigDecimal payAmount = order.getOrder().getPayAmount();
            //前台的提交的应付金额
            BigDecimal payPrice = orderSubmitVo.getPayPrice();
            if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {
                //金额对比
                //保存订单
                saveOrder(order);
                //库存锁定, 只要有异常，回滚订单数据
                WareLockVo wareLockVo = new WareLockVo();
                wareLockVo.setOrderSn(order.getOrder().getOrderSn());
                List<OrderItemVo> collect = order.getOrderItemEntities().stream().map(item -> {
                    OrderItemVo orderItemVo = new OrderItemVo();
                    orderItemVo.setSkuId(item.getSkuId());
                    orderItemVo.setCount(item.getSkuQuantity());
                    orderItemVo.setTitle(item.getSkuName());
                    return orderItemVo;
                }).collect(Collectors.toList());
                wareLockVo.setLocks(collect);
                R r = waresFeignService.orderWareLock(wareLockVo);
                if (r.getCode() == 0) {
                    //锁定库存成功
                    orderSubmitResponseVo.setOrder(order.getOrder());
                } else {
                    String message = (String) r.get("msg");
                    orderSubmitResponseVo.setCode(3);
                }
                return orderSubmitResponseVo;

            } else {
                orderSubmitResponseVo.setCode(2);
                return orderSubmitResponseVo;
            }
        }
    }

    /**
     * 保存订单
     *
     * @param order
     */
    private void saveOrder(OrderCreateTo order) {
        OrderEntity orderEntity = order.getOrder();
        orderEntity.setModifyTime(new Date());
        orderDao.insert(orderEntity);
        List<OrderItemEntity> orderItemEntities = order.getOrderItemEntities();
        orderItemService.saveBatch(orderItemEntities);

    }

    /**
     * 创建订单
     *
     * @return
     */
    private OrderCreateTo createOrder() {
        OrderCreateTo orderCreateTo = new OrderCreateTo();
        //生成一个订单号
        String orderSn = IdWorker.getTimeId();
        OrderEntity orderEntity = buildOrder(orderSn);

        //获取到所有的订单项信息
        List<OrderItemEntity> orderItemEntities = buildOrderItems(orderSn);

        //验价
        computePrice(orderEntity, orderItemEntities);
        orderCreateTo.setOrder(orderEntity);
        orderCreateTo.setOrderItemEntities(orderItemEntities);
        return orderCreateTo;
    }

    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> orderItemEntities) {
        BigDecimal total = new BigDecimal("0.0");
        //订单总额，每一个订单项的金额叠加
        BigDecimal couponAmount = new BigDecimal("0.0");
        BigDecimal promotionAmount = new BigDecimal("0.0");
        BigDecimal integrationAmount = new BigDecimal("0.0");
        BigDecimal giftGrowth = new BigDecimal("0.0");
        BigDecimal giftIntegration = new BigDecimal("0.0");
        for (OrderItemEntity orderItemEntity : orderItemEntities) {
            promotionAmount = promotionAmount.add(orderItemEntity.getPromotionAmount());
            couponAmount = couponAmount.add(orderItemEntity.getCouponAmount());
            integrationAmount = integrationAmount.add(orderItemEntity.getIntegrationAmount());
            total = total.add(orderItemEntity.getRealAmount());
            giftGrowth = giftGrowth.add(new BigDecimal(orderItemEntity.getGiftGrowth().toString()));
            giftIntegration = giftIntegration.add(new BigDecimal(orderItemEntity.getGiftIntegration().toString()));
        }
        orderEntity.setTotalAmount(total);
        //设置应付总额
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));
        orderEntity.setPromotionAmount(promotionAmount);
        orderEntity.setCouponAmount(couponAmount);
        orderEntity.setIntegrationAmount(integrationAmount);
        //积分成长值
        orderEntity.setGrowth(giftGrowth.intValue());
        orderEntity.setIntegration(giftIntegration.intValue());
        //未删除
        orderEntity.setDeleteStatus(0);
    }

    /**
     * 构建订单
     *
     * @param orderSn
     */
    private OrderEntity buildOrder(String orderSn) {
        Long id = LoginUserInterceptor.loginUser.get().getId();
        OrderSubmitVo orderSubmitVo = orderSubmitVoThreadLocal.get();
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(orderSn);
        orderEntity.setMemberId(id);
        //获取收获地址信息
        R r = waresFeignService.getFare(orderSubmitVo.getAddrId());
        FareVo fareData = r.getData(new TypeReference<FareVo>() {
        });
        BigDecimal fare = fareData.getFare();
        orderEntity.setFreightAmount(fare);
        orderEntity.setReceiverDetailAddress(fareData.getAddress().getDetailAddress());
        orderEntity.setMemberUsername(fareData.getAddress().getName());
        orderEntity.setReceiverCity(fareData.getAddress().getCity());
        orderEntity.setReceiverName(fareData.getAddress().getName());
        orderEntity.setReceiverPhone(fareData.getAddress().getPhone());
        orderEntity.setReceiverProvince(fareData.getAddress().getProvince());
        orderEntity.setReceiverPostCode(fareData.getAddress().getPostCode());
        orderEntity.setReceiverRegion(fareData.getAddress().getRegion());

        //设置订单状态
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setAutoConfirmDay(7);

        return orderEntity;
    }

    /**
     * 构建总订单项
     *
     * @return
     */
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        //最后确定每个购物项的价格
        List<OrderItemVo> cartItems = cartFeignService.getCartItems();
        if (!CollectionUtils.isEmpty(cartItems)) {
            return cartItems.stream().map(cartItem -> {
                OrderItemEntity orderItemEntity = buildOrderItem(cartItem);
                orderItemEntity.setOrderSn(orderSn);
                return orderItemEntity;
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * 构建单个订单项
     *
     * @param cartItem
     * @return
     */
    private OrderItemEntity buildOrderItem(OrderItemVo cartItem) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        //订单信息，订单号，（前面已完成）
        //商品的spu信息
        Long skuId = cartItem.getSkuId();
        R r = productFeignService.getSpuInfo(skuId);
        SpuInfoVo spuInfoVo = r.getData(new TypeReference<SpuInfoVo>() {
        });
        orderItemEntity.setSpuId(spuInfoVo.getId());
        orderItemEntity.setSpuBrand(spuInfoVo.getBrandId().toString());
        orderItemEntity.setSpuName(spuInfoVo.getSpuName());
        orderItemEntity.setCategoryId(spuInfoVo.getCatalogId());
        //商品的sku信息
        orderItemEntity.setSkuId(cartItem.getSkuId());
        orderItemEntity.setSkuName(cartItem.getTitle());
        orderItemEntity.setSkuPrice(cartItem.getPrice());
        String skuAttr = StringUtils.collectionToDelimitedString(cartItem.getSkuAttr(), ";");
        orderItemEntity.setSkuAttrsVals(skuAttr);
        orderItemEntity.setSkuQuantity(cartItem.getCount());
        //TODO 优惠信息
        //积分信息
        orderItemEntity.setGiftGrowth(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount().toString())).intValue());
        orderItemEntity.setGiftIntegration(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount().toString())).intValue());

        //订单项的价格信息
        orderItemEntity.setPromotionAmount(new BigDecimal("0.0"));
        orderItemEntity.setCouponAmount(new BigDecimal("0.0"));
        orderItemEntity.setIntegrationAmount(new BigDecimal("0.0"));
        //当前订单项的实际金额
        BigDecimal originPrice = orderItemEntity.getSkuPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity().toString()));
        BigDecimal realPrice = originPrice.subtract(orderItemEntity.getCouponAmount())
                .subtract(orderItemEntity.getPromotionAmount())
                .subtract(orderItemEntity.getIntegrationAmount());
        orderItemEntity.setRealAmount(realPrice);

        return orderItemEntity;
    }
}