package com.wch.gulimall.warehouse.listener;

import com.alibaba.fastjson.TypeReference;
import com.rabbitmq.client.Channel;
import com.sun.org.apache.xerces.internal.impl.dtd.models.DFAContentModel;
import com.wch.common.constant.mq.StockMQConstant;
import com.wch.common.enume.OrderStatusEnum;
import com.wch.common.to.mq.StockDetailTo;
import com.wch.common.to.mq.StockLockedTo;
import com.wch.common.utils.R;
import com.wch.gulimall.warehouse.entity.WareOrderTaskDetailEntity;
import com.wch.gulimall.warehouse.entity.WareOrderTaskEntity;
import com.wch.gulimall.warehouse.service.WareSkuService;
import com.wch.gulimall.warehouse.to.OrderTo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.xml.crypto.Data;
import java.io.IOException;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/5 21:16
 *
 * 监听队列，处理消息
 */
@Service
@RabbitListener(queues = StockMQConstant.STOCK_RELEASE_STOCK_QUEUE)
public class StockListener {

    @Autowired
    private WareSkuService wareSkuService;
    /**
     * 库存自动解锁
     * 库存解锁的场景：
     * 1)下单成功，订单过期木有支付被系统自动取消，被用户手动去雄安
     * 2）下单成功，库存锁定成功，接下来的业务调用失败，导致订单回滚
     * <p>
     * 只要解锁库存的消息失败，不要丢消息，尝试重新解锁
     */
    @RabbitHandler
    private void releaseLockedStock(StockLockedTo stockLockedTo, Message message, Channel channel) throws IOException {
        System.out.println("收到解锁库存消息");
        try {
            wareSkuService.unLockStock(stockLockedTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            //回队
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }

}
