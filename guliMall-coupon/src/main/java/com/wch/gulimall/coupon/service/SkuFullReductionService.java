package com.wch.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wch.common.to.SkuReductionTo;
import com.wch.common.utils.PageUtils;
import com.wch.gulimall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author wch
 * @email 2879924329@qq.com
 * @date 2022-02-20 21:58:00
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuReduction(SkuReductionTo skuReductionTo);
}

