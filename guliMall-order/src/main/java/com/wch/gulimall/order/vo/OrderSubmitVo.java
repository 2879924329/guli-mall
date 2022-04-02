package com.wch.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/2 21:26
 * 订单提交的数据
 */
@Data
public class OrderSubmitVo {
    private Long addrId;
    private String payType;
    //无需提交需要购买的商品，重新去购物车服务获取即可
    //TODO 优惠发牌
    private String orderToken;
    /**
     * 重新验价
     */
    private BigDecimal payPrice;

    //用户相关信息去session中取

    /**
     * 订单备注
     */
    private String note;
}
