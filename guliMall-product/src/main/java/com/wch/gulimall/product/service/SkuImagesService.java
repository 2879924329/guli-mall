package com.wch.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wch.common.utils.PageUtils;
import com.wch.gulimall.product.entity.SkuImagesEntity;

import java.util.List;
import java.util.Map;

/**
 * sku图片
 *
 * @author wch
 * @email 2879924329@qq.com
 * @date 2022-02-20 20:19:13
 */
public interface SkuImagesService extends IService<SkuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuImagesEntity> getImagesById(Long skuId);
}

