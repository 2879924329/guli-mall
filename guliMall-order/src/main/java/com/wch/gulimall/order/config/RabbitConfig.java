package com.wch.gulimall.order.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/28 21:47
 */
@Slf4j
@Configuration
public class RabbitConfig {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 自定义转换工具,使用json序列化机制，进行消息转换
     * @return
     */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 定制RabbitTemplate
     * 服务器收到消息就回调
     *   1，spring.rabbitmq.publisher-confirm-type: correlated
     *   2, 设置确认回调 ConfirmCallback
     *消息抵达队列，进行回调
     *       #发送端消息抵达队列确认
     *     spring.rabbitmq.publisher-returns: true
     *     #只要抵达队列，以异步方式优先回调returnConfirm
     *     spring.rabbitmq.template:
     *       mandatory: true
     *     设置确认回调：  ReturnCallback
     *
     * 3， 消费端确认，保证每一个消息被正确消费，此时才可以删除消息
     *      1，默认是自动确认的，只要消息接收到，客户端会自动确认，服务器会移除这个消息
     *      问题：收到很多消息，自动回复给服务器ack，只有一个消息处理成功，客户端宕机了，木有正确处理的消息被服务器给删除了
     *      手动确认，只要木有明确告诉rabbitmq消息已经被消费，消息就一直是unacked状态，即使consumer宕机，消息不会丢失，会重新变成
     *      ready状态，下一次有新的consumer连接进来，就重新发送给他。
     *     2， 如何手动确认消息？  channel.basicAck(deliveryTag, false);
     *         拒绝消费消息，basicNack(long deliveryTag, boolean multiple, boolean requeue)  其中参数boolean requeue是否重新入队再次发送。
     *
     *      手动确认yml设置：
     *          #手动确认消息
     *            listener:
     *              simple:
     *                 acknowledge-mode: manual
     */
    @Bean
    public void initRabbitTemplate(){
        /**
         * 设置确认回调
         */
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * 只要消息抵达服务器，ack=true
             *
             * @param correlationData 消息的唯一id
             * @param b 消息是否成功收到
             * @param s 失败原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
               log.warn("confirm...{}， ack: {}, 失败原因：{}", correlationData, b, s);
            }
        });
        /**
         * 设置消息抵达队列的确认回调
         */
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             * 只要消息木有投递到指定队列，就触发这个失败回调
             * @param message 投递消息失败的详细信息
             * @param i 回复状态码
             * @param s 回复的文本内容
             * @param s1 当时消息是发送给哪个交换机
             * @param s2 当时消息是用哪个routeKey
             * returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey)
             */
            @Override
            public void returnedMessage(Message message, int i, String s, String s1, String s2) {
                log.warn("message: {}, code: {}, replayText: {} exchange: {}, routeKey: {}", message, i, s, s1, s2);
            }
        });
    }

}
