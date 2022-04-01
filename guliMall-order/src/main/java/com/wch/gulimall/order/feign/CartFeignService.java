package com.wch.gulimall.order.feign;


import com.wch.gulimall.order.to.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/31 20:13
 */
@FeignClient("gulimall-cart")
public interface CartFeignService {

    /**
     * 获取当前用户的所有要结算的购物车列表
     * @return
     */
    @GetMapping("/current/cart-items")
    List<OrderItemVo> getCartItems();
}
