package com.wch.gulimall.order.feign;

import com.wch.gulimall.order.to.MemberAddressTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/31 19:57
 */
@FeignClient("gulimall-member")
public interface MemberFeignService {
    @GetMapping("/member/memberreceiveaddress/{memberId}/address")
    List<MemberAddressTo> getAddress(@PathVariable("memberId") Long memberId);
}
