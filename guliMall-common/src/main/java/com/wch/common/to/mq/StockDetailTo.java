package com.wch.common.to.mq;

import lombok.Data;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/5 20:09
 */
@Data
public class StockDetailTo {
    private Long id;
    /**
     * sku_id
     */
    private Long skuId;
    /**
     * sku_name
     */
    private String skuName;
    /**
     * 购买个数
     */
    private Integer skuNum;
    /**
     * 工作单id
     */
    private Long taskId;
    /**
     * 仓库id
     */
    private Long wareId;
    /**
     * 1-已锁定  2-已解锁  3-已扣减
     */
    private Integer lockStatus;
}
