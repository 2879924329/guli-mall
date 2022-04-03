package com.wch.gulimall.warehouse.to;

import lombok.Data;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/3 19:50
 */
@Data
public class LockResultTo {
    private Long skuId;
    private Integer num;
    private boolean locked;
}
