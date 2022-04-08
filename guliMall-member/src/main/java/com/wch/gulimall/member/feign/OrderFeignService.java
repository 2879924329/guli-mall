package com.wch.gulimall.member.feign;

import com.wch.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



import java.util.Map;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/8 21:20
 */
@FeignClient("gulimall-order")
public interface OrderFeignService {
    @PostMapping("/order/order/order-list")
    R getOrderList(@RequestBody Map<String, Object> params);
}
