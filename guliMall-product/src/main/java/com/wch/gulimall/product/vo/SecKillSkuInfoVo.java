package com.wch.gulimall.product.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/12 19:54
 */
@Data
public class SecKillSkuInfoVo {
    /**
     * id
     */
    private Long id;

    /**
     * 秒杀随机码
     */
    private String randomCode;
    /**
     * 活动id
     */
    private Long promotionId;
    /**
     * 活动场次id
     */
    private Long promotionSessionId;
    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;
    /**
     * 秒杀总量
     */
    private Integer seckillCount;
    /**
     * 每人限购数量
     */
    private Integer seckillLimit;
    /**
     * 排序
     */
    private Integer seckillSort;


    //秒杀开始时间
    private Long startTime;
    //秒杀结束时间
    private Long endTime;
}
