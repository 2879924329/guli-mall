package com.wch.gulimall.product.dao;

import com.wch.gulimall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 * 
 * @author wch
 * @email 2879924329@qq.com
 * @date 2022-02-20 20:19:13
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    /**
     * 在指定的所有属性集合里面，挑出检索属性
     * @param attrIdCollect
     * @return
     */
    List<Long> selectSearchAttr(@Param("attrId") List<Long> attrIdCollect);
}
