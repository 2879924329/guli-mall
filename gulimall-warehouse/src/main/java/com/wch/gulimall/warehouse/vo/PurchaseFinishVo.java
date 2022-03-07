package com.wch.gulimall.warehouse.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/7 15:23
 */
@Data
public class PurchaseFinishVo {
    /**
     * 采购单id
     */
    @NotBlank
    private Long id;

    private List<PurchaseItemDoneVo> items;
}
