package com.wch.gulimall.warehouse.feign;

import com.wch.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/1 22:38
 */
@FeignClient("gulimall-member")
public interface MemberFeignService {
    /**
     * 信息
     */
    @RequestMapping("/member/member/info/{id}")
    R addrInfo(@PathVariable("id") Long id);
}
