package com.wch.gulimall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.wch.common.to.MemberEntityTo;
import com.wch.common.utils.R;
import com.wch.gulimall.order.feign.CartFeignService;
import com.wch.gulimall.order.feign.MemberFeignService;
import com.wch.gulimall.order.feign.WaresFeignService;
import com.wch.gulimall.order.interceptor.LoginUserInterceptor;
import com.wch.gulimall.order.to.MemberAddressTo;
import com.wch.gulimall.order.to.OrderItemVo;
import com.wch.gulimall.order.vo.OrderConfirmVo;
import com.wch.gulimall.order.vo.SkuHasStockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wch.common.utils.PageUtils;
import com.wch.common.utils.Query;

import com.wch.gulimall.order.dao.OrderDao;
import com.wch.gulimall.order.entity.OrderEntity;
import com.wch.gulimall.order.service.OrderService;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {


    @Autowired
    private MemberFeignService memberFeignService;

    @Autowired
    private CartFeignService cartFeignService;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private WaresFeignService waresFeignService;

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
        }, threadPoolExecutor).thenRunAsync(()->{
            //异步去库存服务查询库存
            List<OrderItemVo> items = orderConfirmVo.getItems();
            List<Long> collect = items.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
            R skuStock = waresFeignService.getSkuStock(collect);
            List<SkuHasStockVo> hasStockVos = skuStock.getData(new TypeReference<List<SkuHasStockVo>>() {});
            if (!CollectionUtils.isEmpty(hasStockVos)){
                Map<Long, Boolean> stockMap = hasStockVos.stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
                orderConfirmVo.setStocks(stockMap);
            }
        }, threadPoolExecutor);
        //查询用户积分
        Integer integration = memberEntityTo.getIntegration();
        orderConfirmVo.setIntegration(integration);

        CompletableFuture.allOf(addressFuture, cartItemsFuture).get();
        return orderConfirmVo;
    }

}