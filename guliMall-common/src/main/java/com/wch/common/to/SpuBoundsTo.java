package com.wch.common.to;

import lombok.Data;

import java.math.BigDecimal;


/**
 * @author wch
 * @version 1.0
 * @date 2022/3/5 20:59
 *
 * 微服务之间的传输对象
 */
@Data
public class SpuBoundsTo {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
