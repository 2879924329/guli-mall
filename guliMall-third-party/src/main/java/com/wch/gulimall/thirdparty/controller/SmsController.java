package com.wch.gulimall.thirdparty.controller;

import com.wch.common.utils.R;
import com.wch.gulimall.thirdparty.component.SmsComponent;
import com.wch.gulimall.thirdparty.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/21 19:36
 */
@RestController
@RequestMapping("/sms")
public class SmsController {

    @Autowired
    private SmsService smsService;

    @GetMapping("/send-code")
    public R send(@RequestParam("phone") String  phone) {
        boolean b = smsService.send(phone);
        if (b) {
            return R.ok();
        }else {
            throw new RuntimeException("验证码发送失败");
        }
    }
}
