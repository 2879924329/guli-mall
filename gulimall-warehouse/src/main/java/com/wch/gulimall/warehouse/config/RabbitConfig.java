package com.wch.gulimall.warehouse.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/28 21:47
 */
@Slf4j
@Configuration
public class RabbitConfig {


    /**
     * 自定义转换工具,使用json序列化机制，进行消息转换
     * @return
     */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Exchange stockEventExchange(){
        return new TopicExchange("stock-event-exchange", true, false);
    }
    @Bean
    public Queue stockReleaseQueue(){
        return new Queue("stock.release.stock.queue", true, false, false);
    }
    @Bean
    public Queue stockDelayQueue(){
        HashMap<String, Object> argumentsMap = new HashMap<>();
        argumentsMap.put("x-dead-letter-exchange", "stock-event-exchange");
        argumentsMap.put("x-dead-letter-routing-key", "stock.release");
        //测试期间2分钟
        argumentsMap.put("x-message-ttl", 120000);
        return new Queue("stock.delay.queue", true, false, false, argumentsMap);
    }
    @Bean
    public Binding stockLockedBinding() {
        // Binding(String destination, Binding.DestinationType destinationType, String exchange, String routingKey, @Nullable Map<String, Object> arguments) {
        return new Binding("stock.delay.queue", Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.locked", null);
    }

    @Bean
    public Binding stockReleaseBinding() {
        return new Binding("stock.release.stock.queue", Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.release.#", null);
    }


    /**
     * 第一次连山rabbitmq时才会创建队列，交换机等
     * @param message
     */
    @RabbitListener(queues = "stock.release.stock.queue")
    public void handle(Message message){

    }

}
