package com.wch.gulimall.order.web;

import com.wch.gulimall.order.vo.OrderConfirmVo;
import com.wch.gulimall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.concurrent.ExecutionException;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/29 21:51
 */
@Controller
public class OrderWebController {

    @Autowired
    private OrderService orderService;
    /**
     * 购物车结算
     * @return
     */
    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
       OrderConfirmVo confirmVo = orderService.confirmOrder();
       model.addAttribute("orderConfirmData", confirmVo);
        return "confirm";
    }


}
