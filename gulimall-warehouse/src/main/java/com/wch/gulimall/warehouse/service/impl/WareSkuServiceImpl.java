package com.wch.gulimall.warehouse.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.rabbitmq.client.Channel;
import com.wch.common.constant.mq.StockMQConstant;
import com.wch.common.enume.OrderStatusEnum;
import com.wch.common.to.mq.OrderEntityTo;
import com.wch.common.to.mq.StockDetailTo;
import com.wch.common.to.mq.StockLockedTo;
import com.wch.common.utils.R;
import com.wch.common.exception.NoStockException;
import com.wch.gulimall.warehouse.dao.WareOrderTaskDao;
import com.wch.gulimall.warehouse.dao.WareOrderTaskDetailDao;
import com.wch.gulimall.warehouse.entity.WareOrderTaskDetailEntity;
import com.wch.gulimall.warehouse.entity.WareOrderTaskEntity;
import com.wch.gulimall.warehouse.feign.OrderFeignService;
import com.wch.gulimall.warehouse.feign.ProductFeignService;
import com.wch.gulimall.warehouse.service.WareOrderTaskService;
import com.wch.gulimall.warehouse.to.OrderTo;
import com.wch.gulimall.warehouse.vo.OrderItemVo;
import com.wch.gulimall.warehouse.vo.SkuHasStockVo;
import com.wch.gulimall.warehouse.vo.WareLockVo;
import lombok.Data;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wch.common.utils.PageUtils;
import com.wch.common.utils.Query;

import com.wch.gulimall.warehouse.dao.WareSkuDao;
import com.wch.gulimall.warehouse.entity.WareSkuEntity;
import com.wch.gulimall.warehouse.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;


/**
 * @author WCH
 */

@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {


    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private WareOrderTaskDao wareOrderTaskDao;

    @Autowired
    private WareOrderTaskService wareOrderTaskService;

    @Autowired
    private WareOrderTaskDetailDao wareOrderTaskDetailDao;

    @Autowired
    private OrderFeignService orderFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if (!org.springframework.util.StringUtils.isEmpty(skuId)) {
            queryWrapper.eq("sku_id", skuId);
        }
        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)) {
            queryWrapper.eq("ware_id", wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //1, 判断如果没有这个库存记录，则直接插入
        Integer count = baseMapper.selectCount(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (count > 0) {
            this.baseMapper.addStock(skuId, wareId, skuNum);
        } else {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            //远程调用查skuName
            //调用失败，事务无需回滚
            try {
                R info = productFeignService.info(skuId);
                Map<String, Object> map = (Map<String, Object>) info.get("skuInfo");
                if (info.getCode() == 0) {
                    wareSkuEntity.setSkuName((String) map.get("skuName"));
                }
            } catch (Exception e) {

            }
            this.baseMapper.insert(wareSkuEntity);
        }
    }

    /**
     * 查询库存
     *
     * @param skuIds
     * @return
     */
    @Override
    public List<SkuHasStockVo> getSkuStock(List<Long> skuIds) {
        return skuIds.stream().map(skuId -> {
            SkuHasStockVo skuHasStockVo = new SkuHasStockVo();
            //查询库存总量
            //   select sum(stock - stock_locked) from wms_ware_sku where sku_id = #{skuId}
            Long count = baseMapper.getSkuStock(skuId);
            skuHasStockVo.setSkuId(skuId);
            skuHasStockVo.setHasStock(count != null && count > 0);
            return skuHasStockVo;
        }).collect(Collectors.toList());
    }

    /**
     * 为某个订单锁定库存
     *
     * @param wareLockVo
     * @return 库存解锁的场景：
     * 1)下单成功，订单过期木有支付被系统自动取消，被用户手动去雄安
     * 2）下单成功，库存锁定成功，接下来的业务调用失败，导致订单回滚
     */
    @Transactional(rollbackFor = NoStockException.class)
    @Override
    public Boolean lockOrderWare(WareLockVo wareLockVo) {
        //保存库存工资单详情
        WareOrderTaskEntity wareOrderTaskEntity = new WareOrderTaskEntity();
        wareOrderTaskEntity.setOrderSn(wareLockVo.getOrderSn());
        wareOrderTaskDao.insert(wareOrderTaskEntity);
        //找到每个商品在哪个仓库有库存
        List<OrderItemVo> wareLockVoLocks = wareLockVo.getLocks();
        List<SkuWareHasStock> collect = wareLockVoLocks.stream().map(item -> {
            SkuWareHasStock skuWareHasStock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            skuWareHasStock.setSkuId(skuId);
            skuWareHasStock.setNum(item.getCount());
            skuWareHasStock.setSkuName(item.getTitle());
            List<Long> wareIds = this.baseMapper.listWareIdHasStock(skuId);
            skuWareHasStock.setWareId(wareIds);
            return skuWareHasStock;
        }).collect(Collectors.toList());
        for (SkuWareHasStock skuWareHasStock : collect) {
            boolean skuStocked = false;
            Long skuId = skuWareHasStock.getSkuId();
            List<Long> wareId = skuWareHasStock.getWareId();
            if (CollectionUtils.isEmpty(wareId)) {
                throw new NoStockException(skuId);
            }
            //1,如果每一个商品锁定成功，将当前商品锁定了几件的工资单记录发送给mq
            //2，锁定失败，前面保存的工资单消息就回滚了，发送出去的消息即使要解锁记录，由于去数据库查不到指定id，所以就不用解锁（不合理）
            for (Long id : wareId) {
                //成功就返回1，否则就是0
                Long count = this.baseMapper.lockSkuStock(skuId, id, skuWareHasStock.getNum());
                if (count == 1) {
                    skuStocked = true;
                    //发送消息，告诉mq库存锁定成功
                    //保存工作的详情
                    WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity(null, skuId, skuWareHasStock.getSkuName(), skuWareHasStock.getNum(), wareOrderTaskEntity.getId(), id, 1);
                    wareOrderTaskDetailDao.insert(wareOrderTaskDetailEntity);
                    //发送消息
                    StockLockedTo stockLockedTo = new StockLockedTo();
                    stockLockedTo.setId(wareOrderTaskEntity.getId());
                    //只发id不行，防止前面的数据回滚以后找不到数据
                    StockDetailTo stockDetailTo = new StockDetailTo();
                    BeanUtils.copyProperties(wareOrderTaskDetailEntity, stockDetailTo);
                    stockLockedTo.setStockDetailTo(stockDetailTo);
                    rabbitTemplate.convertAndSend(StockMQConstant.STOCK_EVENT_EXCHANGE, StockMQConstant.STOCK_LOCKED_ROUTE_KEY, stockLockedTo);
                    break;
                }
                //当前仓库失败，尝试下一个
            }
            if (Boolean.FALSE.equals(skuStocked)) {
                //当前商品的所有仓库都没有库存
                throw new NoStockException(skuId);
            }
        }
        return true;
    }

    @Override
    public void unLockStock(StockLockedTo stockLockedTo) {
        System.out.println("收到解锁库存的消息");
        StockDetailTo stockDetailTo = stockLockedTo.getStockDetailTo();
        //工作单的详情id
        Long detailToId = stockDetailTo.getId();
        //查询数据库关于中国订单的库存消息
        //有 ：证明库存锁定成功了，是否解锁还要看订单，没有这个订单，无需解锁，有这个订单，不是解锁库存，看订单的状态
        //已取消：解锁库存
        //没库存，不能解锁
        //没有：库存本身就锁定失败了，无需解锁
        WareOrderTaskDetailEntity wareOrderTaskDetailEntity = wareOrderTaskDetailDao.selectById(detailToId);
        if (!ObjectUtils.isEmpty(wareOrderTaskDetailEntity)) {
            //解锁
            Long id = stockLockedTo.getId();
            WareOrderTaskEntity wareOrderTaskEntity = wareOrderTaskDao.selectById(id);
            String orderSn = wareOrderTaskEntity.getOrderSn();
            //根据订单号查询订单的状态
            R r = orderFeignService.getOrderStatus(orderSn);
            if (r.getCode() == 0) {
                OrderTo data = r.getData(new TypeReference<OrderTo>() {
                });
                //订单不存在
                //订单已取消，解锁库存
                if (ObjectUtils.isEmpty(data) || OrderStatusEnum.CANCLED.getCode().equals(data.getStatus())) {
                    //当前工作单的状态是状态1，已锁定但是未解锁，才可以发消息解锁
                    if (wareOrderTaskDetailEntity.getLockStatus() == 1) {
                        unLocked(stockDetailTo.getSkuId(), stockDetailTo.getWareId(), stockDetailTo.getSkuNum(), detailToId);
                    }
                }
            } else {
                //消息拒绝以后，重新放到队列，让其他消费者继续消费
                throw new RuntimeException("远程服务失败");
            }
        } else {
            //无需解锁
        }
    }

    /**
     * 订单关闭，库存解锁
     * 防止订单服务卡顿，导致订单状态信息改不了，库存消息优先到期，结果查订单状态为新建状态，然后就不会处理，导致卡顿的订单永远无法解锁库存
     *
     * @param orderEntityTo
     */
    @Transactional
    @Override
    public void unLockStock(OrderEntityTo orderEntityTo) {
        String orderSn = orderEntityTo.getOrderSn();
        //查询库存最新解锁状态
        WareOrderTaskEntity wareOrderTaskEntity = wareOrderTaskService.getOrderTaskByOrderSn(orderSn);
        Long id = wareOrderTaskEntity.getId();
        //按照工作单id找到木有解锁的库存进行解锁
        List<WareOrderTaskDetailEntity> wareOrderTaskDetailEntities = wareOrderTaskDetailDao.selectList(new QueryWrapper<WareOrderTaskDetailEntity>().eq("task_id", id)
                .eq("lock_status", 1));
        for (WareOrderTaskDetailEntity wareOrderTaskDetailEntity : wareOrderTaskDetailEntities) {
            unLocked(wareOrderTaskDetailEntity.getSkuId(), wareOrderTaskDetailEntity.getWareId(), wareOrderTaskDetailEntity.getSkuNum(), wareOrderTaskDetailEntity.getId());
        }

    }

    private void unLocked(Long skuId, Long wareId, Integer num, Long taskDetailId) {
        //库存解锁，更新库存工作单状态
        this.baseMapper.unLockStock(skuId, wareId, num);
        WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity();
        wareOrderTaskDetailEntity.setId(taskDetailId);
        //已解锁
        wareOrderTaskDetailEntity.setLockStatus(2);
        wareOrderTaskDetailDao.updateById(wareOrderTaskDetailEntity);
    }

    @Data
    static class SkuWareHasStock {
        private Long skuId;
        private String skuName;
        private Integer num;
        private List<Long> wareId;
    }


}