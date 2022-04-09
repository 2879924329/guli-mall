package com.wch.gulimall.order.web;

import com.wch.gulimall.order.service.OrderService;
import com.wch.gulimall.order.vo.PayAsyncVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/9 20:17
 * <p>
 * 监听支付宝支付成功的消息
 */
@Slf4j
@RestController
public class OrderPayedController {


    @Autowired
    private OrderService orderService;
    /**
     * 只要返回success，支付宝不在通知
     * @return
     */
    @PostMapping("/order/alipay/success")
    public String receive(PayAsyncVo payAsyncVo, HttpServletRequest request) {
       /* Map<String, String[]> parameterMap = request.getParameterMap();
        for (String s : parameterMap.keySet()) {
            String parameter = request.getParameter(s);
            log.info("参数名：{}, 参数值：{}",s, parameter);
        }
        log.info("支付宝响应信息：{}", parameterMap);*/
        //修改订单之前验签
        return orderService.handlePayResult(payAsyncVo, request);
    }
}
