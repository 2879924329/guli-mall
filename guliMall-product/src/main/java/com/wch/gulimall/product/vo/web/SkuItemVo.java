package com.wch.gulimall.product.vo.web;

import com.baomidou.mybatisplus.annotation.TableField;
import com.wch.gulimall.product.entity.SkuImagesEntity;
import com.wch.gulimall.product.entity.SkuInfoEntity;
import com.wch.gulimall.product.entity.SpuInfoDescEntity;
import com.wch.gulimall.product.vo.SecKillSkuInfoVo;
import lombok.Data;

import java.util.List;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/19 22:05
 * <p>
 * 详情页面的内容
 */
@Data
public class SkuItemVo {

    //1、sku基本信息的获取  pms_sku_info
    private SkuInfoEntity info;

    private boolean hasStock = true;

    //2、sku的图片信息    pms_sku_images
    private List<SkuImagesEntity> images;

    //3、获取spu的销售属性组合
    private List<SkuItemSaleAttrVo> saleAttr;

    //4、获取spu的介绍
    private SpuInfoDescEntity desc;

    //5、获取spu的规格参数信息
    private List<SpuItemAttrGroupVo> groupAttrs;

    //当前商品的秒杀优惠信息
    private SecKillSkuInfoVo secKillSkuInfoVo;
}
