package com.wch.gulimall.order.web;

import com.alipay.api.AlipayApiException;
import com.wch.gulimall.order.config.AlipayTemplate;
import com.wch.gulimall.order.service.OrderService;
import com.wch.gulimall.order.vo.PayVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/8 20:00
 */
@Slf4j
@Controller
public class PayOrderController {


    @Autowired
    private AlipayTemplate alipayTemplate;

    @Autowired
    private OrderService orderService;

    /**
     * 响应的不是json数据，而是一个页面,直接交给浏览器就行
     * 将支付页给浏览器展示
     * 支付成功以后跳到用户订单列表页
     * @param orderSn
     * @return
     * @throws AlipayApiException
     */
    @ResponseBody
    @GetMapping(value = "/pay-orders", produces = "text/html")
    public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {
        //获取支付信息
        PayVo payVo = orderService.getOrderPayInfo(orderSn);
        return alipayTemplate.pay(payVo);
    }
}
