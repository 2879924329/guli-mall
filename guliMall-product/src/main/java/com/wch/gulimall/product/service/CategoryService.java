package com.wch.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wch.common.utils.PageUtils;
import com.wch.gulimall.product.entity.CategoryEntity;
import com.wch.gulimall.product.vo.web.Catelog2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author wch
 * @email 2879924329@qq.com
 * @date 2022-02-20 20:19:13
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 树形分类
     * @return
     */
    List<CategoryEntity> listWithTree();



    void removeMenuByIds(List<Long> asList);

    Long[] findCateLogPath(Long catelogId);

    void updateCascade(CategoryEntity category);


    List<CategoryEntity> getLevelFirstCategorys();

    Map<String, List<Catelog2Vo>> getCatelogJson();
}

