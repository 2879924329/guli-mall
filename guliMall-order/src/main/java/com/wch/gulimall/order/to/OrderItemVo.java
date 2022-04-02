package com.wch.gulimall.order.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/31 19:49
 */
@Data
public class OrderItemVo {
    private Long skuId;
    private String title;
    private String image;
    /**
     * 属性
     */
    private List<String> skuAttr;
    private BigDecimal price;
    private Integer count;
    private BigDecimal totalPrice;

    private BigDecimal weight;
}
