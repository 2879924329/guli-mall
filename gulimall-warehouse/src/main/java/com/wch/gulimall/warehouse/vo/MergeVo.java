package com.wch.gulimall.warehouse.vo;

import lombok.Data;

import java.util.List;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/7 14:12
 */
@Data
public class MergeVo {
    private Long purchaseId;
    private List<Long> items;
}
