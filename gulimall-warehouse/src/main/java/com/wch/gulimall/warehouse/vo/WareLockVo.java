package com.wch.gulimall.warehouse.vo;

import lombok.Data;

import java.util.List;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/3 19:46
 *
 * 锁定库存vo类
 */
@Data
public class WareLockVo {
    private String orderSn;
    private List<OrderItemVo> locks;
}
