package com.wch.gulimall.order;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import java.util.HashMap;

/**
 * 使用RabbitMQ，
 *  1， 引入amqp场景，RabbitAutoConfiguration自动生效
 *  2， 给容器中自动配置了 RabbitTemplate， AmqpAdmin ，CachingConnectionFactory ， RabbitMessagingTemplate
 *      所有的属性都是：@ConfigurationProperties(prefix = "spring.rabbitmq")
 *      给配置文件中配置spring.rabbitmq开头的信息
 *  3，@EnableRabbit，开启功能
 *  4, 监听消息：@RabbitListener ：类和方法，监听哪些队列的消息 （必须有@EnableRabbit）
 *              @RabbitHandler ： 必须是方法 ， 重载区分不同的消息
 */
@EnableRedisHttpSession
@EnableDiscoveryClient
@EnableRabbit
@EnableFeignClients
@SpringBootApplication
public class GuliMallOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(GuliMallOrderApplication.class, args);
    }

}
