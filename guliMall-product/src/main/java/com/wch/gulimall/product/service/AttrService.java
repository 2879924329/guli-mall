package com.wch.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wch.common.utils.PageUtils;
import com.wch.gulimall.product.entity.AttrEntity;
import com.wch.gulimall.product.vo.AttrGroupVo;
import com.wch.gulimall.product.vo.AttrResponseVo;
import com.wch.gulimall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author wch
 * @email 2879924329@qq.com
 * @date 2022-02-20 20:19:13
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attr);

    PageUtils queryBaseAttr(Map<String, Object> params, Long catelogId, String attrType);

    AttrResponseVo getAttrInfo(Long attrId);

    void updateAttr(AttrResponseVo attr);

    List<AttrEntity> getRelationAttrInfo(Long attrgroupId);

    void deleteRelation(AttrGroupVo[] attrGroupVos);

    PageUtils getRelationNoAttrInfo(Map<String, Object> params, Long attrgroupId);

    List<Long> selectSearchAttrs(List<Long> attrIdCollect);
}

