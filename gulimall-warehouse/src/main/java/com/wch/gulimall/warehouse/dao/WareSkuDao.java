package com.wch.gulimall.warehouse.dao;

import com.wch.gulimall.warehouse.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品库存
 * 
 * @author wch
 * @email 2879924329@qq.com
 * @date 2022-02-20 22:27:21
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
    /**
     * 入库
     * @param skuId sku id
     * @param wareId wareId
     * @param skuNum number
     */
    void addStock(@Param("skuId") Long skuId,@Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);

    Long getSkuStock(@Param("skuId") Long skuId);
}
