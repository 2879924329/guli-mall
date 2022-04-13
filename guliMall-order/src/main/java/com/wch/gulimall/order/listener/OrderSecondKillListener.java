package com.wch.gulimall.order.listener;

import com.rabbitmq.client.Channel;
import com.wch.common.constant.mq.OrderMQConstant;
import com.wch.common.to.mq.SecondKillOrderTo;
import com.wch.gulimall.order.entity.OrderEntity;
import com.wch.gulimall.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/13 21:00
 */
@Slf4j
@RabbitListener(queues = OrderMQConstant.ORDER_SECKILL_ORDER_QUEUE)
@Component
public class OrderSecondKillListener {
    @Autowired
    private OrderService orderService;
    @RabbitHandler
    public void listener(SecondKillOrderTo order, Channel channel, Message message) throws IOException {
        try {
            log.info("准备创建秒杀单的信息",order.getOrderSn());
            orderService.createSecondKillOrder(order);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch (Exception e){
            //归队
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }

    }
}
