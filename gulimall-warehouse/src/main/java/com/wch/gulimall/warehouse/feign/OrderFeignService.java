package com.wch.gulimall.warehouse.feign;

import com.wch.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/5 20:44
 */
@FeignClient("gulimall-order")
public interface OrderFeignService {
    @GetMapping("/order/order/status/{orderSn}")
     R getOrderStatus(@PathVariable("orderSn") String orderSn);
}
