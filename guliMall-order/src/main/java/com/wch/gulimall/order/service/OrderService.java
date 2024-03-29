package com.wch.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wch.common.to.mq.SecondKillOrderTo;
import com.wch.common.utils.PageUtils;
import com.wch.gulimall.order.entity.OrderEntity;
import com.wch.gulimall.order.vo.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author wch
 * @email 2879924329@qq.com
 * @date 2022-02-20 22:14:34
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    OrderSubmitResponseVo submitOrder(OrderSubmitVo orderSubmitVo);

    OrderEntity getOrderStatusByOrderSn(String orderSn);

    void closeOrder(OrderEntity order);

    PayVo getOrderPayInfo(String orderSn);

    PageUtils queryOrderListPage(Map<String, Object> params);

    String handlePayResult(PayAsyncVo payAsyncVo, HttpServletRequest request);

    void createSecondKillOrder(SecondKillOrderTo order);
}

