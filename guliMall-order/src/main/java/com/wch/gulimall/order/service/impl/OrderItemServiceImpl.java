package com.wch.gulimall.order.service.impl;

import com.rabbitmq.client.Channel;
import com.wch.gulimall.order.entity.OrderEntity;
import com.wch.gulimall.order.entity.OrderReturnReasonEntity;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wch.common.utils.PageUtils;
import com.wch.common.utils.Query;

import com.wch.gulimall.order.dao.OrderItemDao;
import com.wch.gulimall.order.entity.OrderItemEntity;
import com.wch.gulimall.order.service.OrderItemService;

//@RabbitListener(queues = {"hello-java-queue"})
@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }


    /**
     * 监听信息
     * 声明要监听的队列数组，queues = {"hello-java-queue"}
     * 消息类型：
     *    原生的消息类型：class org.springframework.amqp.core.Message
     *    T<发送的消息类型> OrderReturnReasonEntity returnReasonEntity
     *
     * Channel channel: 当前传输数据的通道
     *
     *
     * Queue:可以有很多人来监听消息，只要收到消息，队列就会删除消息，而且只能有一个人收到此消息
     *   1)订单服务启动多个，同一个消息只能有一个客户端收到
     *   2）只有一个消息完全处理完，方法运行结束，歪蜜就可以接收下一个消息
     */
    //@RabbitListener(queues = {"hello-java-queue"})
    //@RabbitHandler
    public void receiveMsg(Message message, OrderReturnReasonEntity returnReasonEntity,
                           Channel channel) throws InterruptedException {
        //消息体：Body:'{"id":1,"name":"hhha","sort":3,"status":1,"createTime":1648476351496}'
        System.out.println("接收到的消息内容：" + message + "类型：" + returnReasonEntity);
        byte[] body = message.getBody();
        //消息头信息
        MessageProperties messageProperties = message.getMessageProperties();
        Thread.sleep(3000);
        System.out.println("消息处理完成"+returnReasonEntity.getName());
        //按顺序自增
        long deliveryTag = messageProperties.getDeliveryTag();
        System.out.println("deliveryTag: " + deliveryTag);

        try {
            //确认消息, 挨个确认，非批量
            channel.basicAck(deliveryTag, false);
            //channel.basicNack();
        } catch (IOException e) {
            //网络中断
            e.printStackTrace();
        }
    }

   // @RabbitHandler
    public void receiveMsg(OrderEntity order)  {
        System.out.println("接收到的消息内容：" + order);
    }

}