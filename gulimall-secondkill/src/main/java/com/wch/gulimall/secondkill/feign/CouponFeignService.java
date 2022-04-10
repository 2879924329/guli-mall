package com.wch.gulimall.secondkill.feign;

import com.wch.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/10 21:36
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService {
    @GetMapping("/coupon/seckillsession/last-there")
    R getLastSessions();
}
