package com.wch.common.to.mq;

import lombok.Data;

import java.util.List;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/5 20:03
 */
@Data
public class StockLockedTo {
    /**
     * 库存工作单id
     */
    private Long id;

    /**
     * 工资单详情的id
     */
    private StockDetailTo stockDetailTo;
}
