package com.wch.gulimall.order;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
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
 *
 * 本地事务失效问题：
 * 同一个对象内事务的方法互调默认失效，原因：绕过了代理对象，事务是使用代理对象来控制的
 * 解决：使用代理对象来调用事务
 *  1)引入spring-boot-starter-aop:引入了aspectjweaver
 *  2）@EnableAspectJAutoProxy(exposeProxy = true), 开启aspectj动态代理功能， 以后的所有的动态代理都是aspectj创建的（即使木有接口也可以创建动态代理）
 *  3）本类用代理对象互调
 */
@EnableAspectJAutoProxy(exposeProxy = true)
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
