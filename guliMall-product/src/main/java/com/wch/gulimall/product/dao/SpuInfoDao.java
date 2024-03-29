package com.wch.gulimall.product.dao;

import com.wch.gulimall.product.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 * 
 * @author wch
 * @email 2879924329@qq.com
 * @date 2022-02-20 20:19:13
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    /**
     * 更新商品状态
     * @param spuId
     * @param code
     */
    void updateSpuStatus(@Param("spuId") Long spuId, @Param("code") int code);
}
