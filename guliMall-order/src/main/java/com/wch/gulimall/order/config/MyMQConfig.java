package com.wch.gulimall.order.config;

import com.rabbitmq.client.Channel;
import com.wch.gulimall.order.entity.OrderEntity;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/4 21:23
 */
@Configuration
public class MyMQConfig {

    /**
     * 容器中的binding，queue，exchange都会自动创建，
     * 一但创建队列以后，即使属性发生变化，也不会覆盖原队列
     * @return
     */
    @Bean
    public Queue orderDelayQueue() {
        //Queue(String name, boolean durable, boolean exclusive, boolean autoDelete, @Nullable Map<String, Object> arguments)
        HashMap<String, Object> argumentsMap = new HashMap<>();
        argumentsMap.put("x-dead-letter-exchange", "order-event-exchange");
        argumentsMap.put("x-dead-letter-routing-key", "order.release.order");
        argumentsMap.put("x-message-ttl", 60000);
        return new Queue("order.delay.queue", true, false, false, argumentsMap);
    }

    @Bean
    public Queue orderReleaseOrderQueue() {
        return new Queue("order.release.order.queue", true, false, false);
    }

    @Bean
    public Exchange orderEventExchange() {
        return new TopicExchange("order-event-exchange", true, false);
    }

    @Bean
    public Binding orderCreateOrder() {
        // Binding(String destination, Binding.DestinationType destinationType, String exchange, String routingKey, @Nullable Map<String, Object> arguments) {
        return new Binding("order.delay.queue", Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.create.order", null);
    }

    @Bean
    public Binding orderReleaseOrder() {
        return new Binding("order.release.order.queue", Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.order", null);
    }


    @RabbitListener(queues = "order.release.order.queue")
    public void listener(OrderEntity order, Channel channel, Message message) throws IOException {
        System.out.println("收到过期订单信息，准备关闭订单"+order.getOrderSn());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
