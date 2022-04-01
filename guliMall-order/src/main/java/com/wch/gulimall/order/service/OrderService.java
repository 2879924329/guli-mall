package com.wch.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wch.common.utils.PageUtils;
import com.wch.gulimall.order.entity.OrderEntity;
import com.wch.gulimall.order.vo.OrderConfirmVo;

import java.util.Map;

/**
 * 订单
 *
 * @author wch
 * @email 2879924329@qq.com
 * @date 2022-02-20 22:14:34
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVo confirmOrder();
}

