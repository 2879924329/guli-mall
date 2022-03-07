package com.wch.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wch.common.utils.PageUtils;
import com.wch.gulimall.order.entity.OrderOperateHistoryEntity;

import java.util.Map;

/**
 * 订单操作历史记录
 *
 * @author wch
 * @email 2879924329@qq.com
 * @date 2022-02-20 22:14:34
 */
public interface OrderOperateHistoryService extends IService<OrderOperateHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

