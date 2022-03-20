package com.wch.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wch.common.utils.PageUtils;
import com.wch.gulimall.product.entity.BrandEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌
 *
 * @author wch
 * @email 2879924329@qq.com
 * @date 2022-02-20 20:19:13
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void updateDetail(BrandEntity brand);

    List<BrandEntity> getBrandByIds(List<Long> brandIds);
}

