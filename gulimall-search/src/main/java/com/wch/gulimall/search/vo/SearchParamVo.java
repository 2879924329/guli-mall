package com.wch.gulimall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/15 21:13
 *
 * 封装页面所有可能传递过来的关键字
 * catalog3Id=255&keyword=小米&sort = saleCount_asc/desc&hasStock = 0 / 1
 */
@Data
public class SearchParamVo {

    /**
     * 页面传递过来的全文匹配关键字
     */
    private String keyword;

    /**
     * 三级分类id
     */
    private Long catalog3Id;

    /**
     * 排序条件
     * sort = saleCount_asc/desc
     * sort = skuPrice_asc/desc
     * sort = hotScore_asc/desc
     */
    private String sort;

    /**
     * 是否有货
     * hasStock = 0 / 1
     */
    private Integer hasStock = 1;

    /**
     * 价格区间
     * skuPrice = 1_500/500_1000/1000
     *
     */
    private String skuPrice;
    /**
     * 品牌id
     * brandId = 1
     * attr = 2.5寸
     */
    private List<Integer> brandId;
    /**
     * 商品属性
     *  attr = 2.5寸。。。
     */
    private List<String> attrs;

    /**
     * 页码
     */
    private Integer pageNumber = 1;


}
