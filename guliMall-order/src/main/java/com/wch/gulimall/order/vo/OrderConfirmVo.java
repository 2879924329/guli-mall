package com.wch.gulimall.order.vo;

import com.wch.gulimall.order.to.MemberAddressTo;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/31 19:43
 *
 * 订单确认消息vo
 */
@Data
public class OrderConfirmVo {
    /**
     * 用户的收获地址
     */
    private List<MemberAddressTo> address;

    /**
     * 所有选中的购物项
     */
    private List<OrderItemVo> items;

    public Integer getCount(){
        Integer count = 0;
        for (OrderItemVo item : items) {
            count += item.getCount();
        }
        return count;
    }

    //TODO 发票记录
    /**
     * 优惠券信息
     */
    private Integer integration;

    /**
     * 订单防重令牌
     */
    private String orderToken;

    private Map<Long, Boolean> stocks;




    public BigDecimal getTotal(){
        BigDecimal totalPrice = new BigDecimal("0");
        if (!CollectionUtils.isEmpty(items)){
            for (OrderItemVo item : items) {
                BigDecimal multiply = item.getPrice().multiply(new BigDecimal(item.getCount().toString()));
                totalPrice = totalPrice.add(multiply);
            }

        }
        return totalPrice;
    }
    /**
     * 实际应付金额
     */
    public BigDecimal getPayPrice() {
     return this.getTotal();
    }

}
