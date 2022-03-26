package com.wch.gulimall.cart.vo;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/26 19:44
 * 整个购物车VO
 */
@Data
public class CartVo {
    /**
     * 购物项
     */
    private List<CartItemVo> cartItemVos;

    /**
     * 商品数量
     */
    private Integer countNumber;

    /**
     * 商品类型
     */
    private Integer countType;

    /**
     * 商品总价
     */
    private BigDecimal totalAmount;

    /**
     * 减免
     */
    private BigDecimal reduce = new BigDecimal("0");

    /**
     * 获取商品总共几件
     * @return
     */
    public Integer getCountNum(){
        int count = 0;
        if (!CollectionUtils.isEmpty(cartItemVos)){
            for (CartItemVo cartItemVo : cartItemVos) {
                count += cartItemVo.getCount();
            }
        }
        return count;
    }

    /**
     * 商品类型有几种
     * @return
     */
    public Integer getItemType(){
        if (!CollectionUtils.isEmpty(cartItemVos)){
            return cartItemVos.size();
        }else {
            return 0;
        }
    }

    /**
     * 计算购物车总价格
     * @return
     */
    public BigDecimal getTotalAmount(){
        BigDecimal amount = new BigDecimal("0");
        for (CartItemVo cartItemVo : cartItemVos) {
            BigDecimal totalPrice = cartItemVo.getTotalPrice();
            amount = amount.add(totalPrice);
        }
        return amount.subtract(this.getReduce());
    }

}
