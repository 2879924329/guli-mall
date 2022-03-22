package com.wch.gulimall.authserver.controller;

import com.wch.common.utils.R;
import com.wch.gulimall.authserver.feign.ThirdPartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/20 21:09
 */
@Controller
public class LoginController {


    /**
     * 发送一个请求直接跳转一个页面，
     * 使用mvc viewController将请求直接映射
     *
     */


    @Autowired
    private ThirdPartService thirdPartService;

    @ResponseBody
    @GetMapping("/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone){
        thirdPartService.send(phone);
        return R.ok();
    }
}
