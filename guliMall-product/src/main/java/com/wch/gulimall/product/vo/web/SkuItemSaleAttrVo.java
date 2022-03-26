package com.wch.gulimall.product.vo.web;

import lombok.Data;

import java.util.List;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/26 22:26
 */
@Data
public class SkuItemSaleAttrVo {

    private Long attrId;

    private String attrName;

    private List<AttrValueWithSkuIdVo> attrValues;
}
