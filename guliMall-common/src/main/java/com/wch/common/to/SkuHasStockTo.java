package com.wch.common.to;

import lombok.Data;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/9 22:07
 */
@Data
public class SkuHasStockTo {
    private Long SkuId;
    private Boolean hasStock;
}
