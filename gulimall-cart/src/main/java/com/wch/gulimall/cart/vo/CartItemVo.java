package com.wch.gulimall.cart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/26 19:45
 * 购物车的购物项
 */
@Data
public class CartItemVo {
    private Long skuId;
    private Boolean checked = true;
    private String title;
    private String image;
    /**
     * 属性
     */
    private List<String> skuAttr;
    private BigDecimal price;
    private Integer count;
    private BigDecimal totalPrice;

    /**
     * 计算购物项的总价格
     * @return
     */
    public BigDecimal getTotalPrice(){
       return this.price.multiply(new BigDecimal("" + this.count));
    }
}
