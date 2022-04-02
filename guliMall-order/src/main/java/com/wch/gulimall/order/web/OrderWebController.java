package com.wch.gulimall.order.web;

import com.wch.gulimall.order.vo.OrderConfirmVo;
import com.wch.gulimall.order.service.OrderService;
import com.wch.gulimall.order.vo.OrderSubmitResponseVo;
import com.wch.gulimall.order.vo.OrderSubmitVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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

    /**
     * 提交订单
     * @param orderSubmitVo
     * @return
     */
    @PostMapping("/submit-order")
    public String submitOrder(OrderSubmitVo orderSubmitVo){
        //创建订单，验证令牌，验证价格，锁库存
        //下单成功到支付选项页面
        //下单失败到订单确认页
       OrderSubmitResponseVo orderResponseVo = orderService.submitOrder(orderSubmitVo);
       if (orderResponseVo.getCode() == 0){
           //成功
           return "pay";
       }else {
           //失败，重定向到订单确认页面
           return "redirect:http://order.guli-mall.com/toTrade";
       }

    }

}
