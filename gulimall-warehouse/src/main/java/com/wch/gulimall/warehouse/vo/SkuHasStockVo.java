package com.wch.gulimall.warehouse.vo;

import lombok.Data;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/9 21:46
 */
@Data
public class SkuHasStockVo {
  private Long SkuId;
  private Boolean hasStock;
}
