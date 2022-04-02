package com.wch.gulimall.order.to;

import com.wch.gulimall.order.entity.OrderEntity;
import com.wch.gulimall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/2 21:41
 *
 * 创建订单的返回类
 */
@Data
public class OrderCreateTo {
    private OrderEntity order;
    private List<OrderItemEntity> orderItemEntities;
    private BigDecimal payPrice;
    private BigDecimal fare;
}
