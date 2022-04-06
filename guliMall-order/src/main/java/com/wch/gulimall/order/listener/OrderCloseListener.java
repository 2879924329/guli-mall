package com.wch.gulimall.order.listener;

import com.rabbitmq.client.Channel;
import com.wch.common.constant.mq.OrderMQConstant;
import com.wch.gulimall.order.entity.OrderEntity;
import com.wch.gulimall.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/6 19:53
 */
@Slf4j
@RabbitListener(queues = OrderMQConstant.ORDER_RELEASE_ORDER_QUEUE)
@Service
public class OrderCloseListener {

    @Autowired
    private OrderService orderService;
    @RabbitHandler
    public void listener(OrderEntity order, Channel channel, Message message) throws IOException {
       log.warn("收到过期订单信息，准备关闭订单{}",order.getOrderSn());
        try {
            orderService.closeOrder(order);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch (Exception e){
            //归队
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }

    }
}
