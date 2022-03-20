package com.wch.gulimall.product.vo.web;

import com.wch.gulimall.product.entity.SkuImagesEntity;
import com.wch.gulimall.product.entity.SkuInfoEntity;
import com.wch.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/19 22:05
 *
 * 详情页面的内容
 */
@Data
public class SkuItemVo {
    //sku的基本信息
    SkuInfoEntity skuInfoEntity;
    //sku的图片信息
    List<SkuImagesEntity> images;
    //获取spu的销售属性组合
    List<SkuItemSaleVo> skuItemSaleVos;
    //获取spu的介绍
    SpuInfoDescEntity spuInfoDescEntity;
    //获取spu规格参数信息
    private List<SpuItemBaseAttrVo> groupAttrs;

    @Data
    public static class SkuItemSaleVo{
        private Long attrId;
        private String attrName;
        private List<String> attrValues;
    }
    @Data
    public static class SpuItemBaseAttrVo{
        private String groupName;
        private List<SpuBaseAttrVo> spuBaseAttrVos;
    }

    @Data
    public static class SpuBaseAttrVo{
        private String attrName;
        private String attrValue;
    }

}
