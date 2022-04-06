package com.wch.gulimall.warehouse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wch.common.to.mq.OrderEntityTo;
import com.wch.common.utils.PageUtils;
import com.wch.gulimall.warehouse.entity.WareOrderTaskEntity;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author wch
 * @email 2879924329@qq.com
 * @date 2022-02-20 22:27:21
 */
public interface WareOrderTaskService extends IService<WareOrderTaskEntity> {

    PageUtils queryPage(Map<String, Object> params);

    WareOrderTaskEntity getOrderTaskByOrderSn(String orderSn);
}

