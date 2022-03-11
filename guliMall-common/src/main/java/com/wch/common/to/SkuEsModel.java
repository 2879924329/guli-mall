package com.wch.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/9 21:01
 *
 * es传输数据模型
 */
@Data
public class SkuEsModel {
    private Long spuId;

    private Long skuId;

    private String skuTitle;

    private BigDecimal skuPrice;

    private String skuImg;

    private Long saleCount;

    /**
     * 是否有库存
     */
    private Boolean hasStock;

    /**
     * 热度，数据库设计时没有加入此字段
     */
    private Long hotScore;

    private Long brandId;

    private String brandName;

    private String brandImg;

    private Long catelogId;

    private String catelogName;

    /**
     * sku对应的spu的所有规格属性中，能够被检索的属性集合
     */
    private List<Attrs> attrs;

    @Data
    public static class Attrs {

        private Long attrId;

        private String attrName;

        private String attrValue;
    }
}
