package com.wch.gulimall.order;

import com.wch.gulimall.order.entity.OrderEntity;
import com.wch.gulimall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.UUID;

@Slf4j
@SpringBootTest
class GuliMallOrderApplicationTests {

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 如何创建交换机，queue，binding，如何收发信息
     * 1)使用AmqpAdmin进行创建
     */
    @Test
    void contextLoads() {
        //创建一个交换机
        //DirectExchange(String name, boolean durable, boolean autoDelete, Map<String, Object> arguments)
        DirectExchange hello = new DirectExchange("hello-java-exchange", true, false, null);
        amqpAdmin.declareExchange(hello);
        log.info("交换机[{}]创建完成", "hello-java-exchange");
    }

    @Test
    void createQueue() {
        //创建队列
        //Queue(String name, boolean durable, boolean exclusive, boolean autoDelete, @Nullable Map<String, Object> arguments)
        //boolean exclusive:是否排他，只能当前连接使用
        Queue queue = new Queue("hello-java-queue", true, false, false);
        amqpAdmin.declareQueue(queue);
        log.info("queue[{}]创建完成", "hello-java-queue");
    }

    @Test
    void binding() {
        //创建绑定关系
        //Binding(String destination, Binding.DestinationType destinationType, String exchange, String routingKey, @Nullable Map<String, Object> arguments)
        //String destination:目的地。 Binding.DestinationType destinationType：目的地类型
        //String exchange：交换机。tring routingKey：路径key
        //Map<String, Object> arguments：自定义参数
        //将指定的交换机和目的地绑定，使用routekey作为指定的路由键
        Binding binding = new Binding("hello-java-queue", Binding.DestinationType.QUEUE, "hello-java-exchange", "hello.java", null);
        amqpAdmin.declareBinding(binding);
        log.info("绑定成功");
    }

    @Test
    void sendMsg() {
        //convertAndSend(String exchange, String routingKey, Object object)
        String msg = "hello world";

        //如果发送的信息是对象，使用序列化机制，将对象写出去，对象必须实现Serializable
        //发送的信息对象可以转换成json
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0){
                OrderReturnReasonEntity orderReturnReasonEntity = new OrderReturnReasonEntity();
                orderReturnReasonEntity.setId(1L);
                orderReturnReasonEntity.setCreateTime(new Date());
                orderReturnReasonEntity.setName("haha" + i);
                orderReturnReasonEntity.setStatus(1);
                orderReturnReasonEntity.setSort(3);
                rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", orderReturnReasonEntity);
                log.info("信息发送完成:{}", orderReturnReasonEntity);
            }else {
                OrderEntity orderEntity = new OrderEntity();
                orderEntity.setOrderSn(UUID.randomUUID().toString());
                rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", orderEntity);
            }

        }
    }



}
