package com.wch.gulimall.order.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/29 21:51
 */
@Controller
public class OrderWebController {
    /**
     * 购物车结算
     * @return
     */
    @GetMapping("/toTrade")
    public String toTrade(){
        return "confirm";
    }
}
