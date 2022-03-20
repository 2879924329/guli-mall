package com.wch.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wch.common.utils.PageUtils;
import com.wch.gulimall.product.entity.SkuInfoEntity;
import com.wch.gulimall.product.vo.web.SkuItemVo;

import java.util.List;
import java.util.Map;

/**
 * sku信息
 *
 * @author wch
 * @email 2879924329@qq.com
 * @date 2022-02-20 20:19:13
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuInfo(SkuInfoEntity skuInfoEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);

;

    List<SkuInfoEntity> getSkuInfoById(Long spuId);

    SkuItemVo item(Long skuId);
}

