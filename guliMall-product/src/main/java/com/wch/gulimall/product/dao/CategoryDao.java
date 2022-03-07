package com.wch.gulimall.product.dao;

import com.wch.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author wch
 * @email 2879924329@qq.com
 * @date 2022-02-20 20:19:13
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
