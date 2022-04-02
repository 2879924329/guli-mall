package com.wch.gulimall.order.vo;

import com.wch.gulimall.order.entity.OrderEntity;
import lombok.Data;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/2 21:23
 *
 * 订单提交的返回数据
 */
@Data
public class OrderSubmitResponseVo {
    private OrderEntity order;
    private Integer code;
}
