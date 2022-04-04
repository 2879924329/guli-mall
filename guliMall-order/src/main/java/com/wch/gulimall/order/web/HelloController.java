package com.wch.gulimall.order.web;

import com.wch.gulimall.order.entity.OrderEntity;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.UUID;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/29 21:14
 */
@Controller
public class HelloController {


    @Autowired
    RabbitTemplate rabbitTemplate;
    @GetMapping("/{page}.html")
    public String listPage(@PathVariable("page") String page){
        return page;
    }

    @ResponseBody
    @GetMapping("/test/create-order")
    public String createOrderTest(){
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(UUID.randomUUID().toString());
        orderEntity.setModifyTime(new Date());
        rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order",orderEntity);
        return "ok";
    }
}
