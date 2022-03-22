package com.wch.gulimall.authserver.feign;

import com.wch.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/21 19:41
 */
@FeignClient("gulimall-third-party")
public interface ThirdPartService {
    @GetMapping("/sms/send-code")
    R send(@RequestParam("phone") String  phone);
}
