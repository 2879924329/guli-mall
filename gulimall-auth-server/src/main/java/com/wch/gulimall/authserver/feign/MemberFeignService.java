package com.wch.gulimall.authserver.feign;

import com.wch.common.utils.R;
import com.wch.gulimall.authserver.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/22 21:25
 */
@FeignClient("gulimall-member")
public interface MemberFeignService {
    @PostMapping("/member/member/register")
    R register(@RequestBody UserRegisterVo userRegisterVo);
}
