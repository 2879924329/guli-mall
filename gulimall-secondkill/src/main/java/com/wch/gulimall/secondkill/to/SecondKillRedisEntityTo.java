package com.wch.gulimall.secondkill.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/10 22:23
 */
@Data
public class SecondKillRedisEntityTo {
    /**
     * id
     */
    private Long id;
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

    //sku的详细信息
    private SkuInfoTo skuInfoTo;
}
