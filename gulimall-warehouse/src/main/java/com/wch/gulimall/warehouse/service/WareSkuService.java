package com.wch.gulimall.warehouse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wch.common.utils.PageUtils;
import com.wch.gulimall.warehouse.entity.WareSkuEntity;
import com.wch.gulimall.warehouse.to.LockResultTo;
import com.wch.gulimall.warehouse.vo.SkuHasStockVo;
import com.wch.gulimall.warehouse.vo.WareLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author wch
 * @email 2879924329@qq.com
 * @date 2022-02-20 22:27:21
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockVo> getSkuStock(List<Long> skuIds);

    Boolean lockOrderWare(WareLockVo wareLockVo);
}

