package com.wch.gulimall.member.web;

import com.alibaba.fastjson.JSON;
import com.wch.common.utils.R;
import com.wch.gulimall.member.feign.OrderFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/8 20:49
 */
@Controller
public class MemberWebController {


    @Autowired
    private OrderFeignService orderFeignService;
    /**
     * 支付成功以后的跳转页面
     * @return
     */
    @GetMapping("/member-order.html")
    public String memberOrderPage(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                  Model model){
        HashMap<String, Object> params = new HashMap<>();
        params.put("page", pageNum.toString());
        R r = orderFeignService.getOrderList(params);
        System.out.println(JSON.toJSONString(r));
        model.addAttribute("orders",r);
        return "orderList";
    }
}
