package com.wch.gulimall.order.controller;

import com.wch.common.utils.R;
import com.wch.gulimall.order.entity.OrderEntity;
import com.wch.gulimall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/28 22:34
 */
@Slf4j
@RestController
public class RabbitController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * rabbitMQ发送消息测试
     * @param num
     * @return
     */
    @GetMapping("/sendMsg")
    public String sendMsg(@RequestParam(value = "num", defaultValue = "10") Integer num){
        for (int i = 0; i < num; i++) {
            if (i % 2 == 0){
                OrderReturnReasonEntity orderReturnReasonEntity = new OrderReturnReasonEntity();
                orderReturnReasonEntity.setId(1L);
                orderReturnReasonEntity.setCreateTime(new Date());
                orderReturnReasonEntity.setName("haha" + i);
                orderReturnReasonEntity.setStatus(1);
                orderReturnReasonEntity.setSort(3);
                rabbitTemplate.convertAndSend("hello-java-exchange",
                        "hello.java", orderReturnReasonEntity, new CorrelationData(UUID.randomUUID().toString()));
                log.info("信息发送完成:{}", orderReturnReasonEntity);
            }else {
                OrderEntity orderEntity = new OrderEntity();
                orderEntity.setOrderSn(UUID.randomUUID().toString());
                rabbitTemplate.convertAndSend("hello-java-exchange",
                        "hello22.java", orderEntity, new CorrelationData(UUID.randomUUID().toString()));
            }

        }
        return "ok";
    }



}
