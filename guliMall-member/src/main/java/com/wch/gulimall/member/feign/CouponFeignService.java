package com.wch.gulimall.member.feign;

import com.wch.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author wch
 * @version 1.0
 * @date 2022/2/21 20:19
 *
 * 远程调用接口
 */

@FeignClient("gulimall-coupon")
public interface CouponFeignService {
    /**
     * 远程调用获取会员店额优惠券信息
     * @return
     */
    @RequestMapping("/coupon/coupon/member/list")
    R getMemberCoupons();

}
