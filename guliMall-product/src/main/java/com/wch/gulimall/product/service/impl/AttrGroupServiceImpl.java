package com.wch.gulimall.product.service.impl;

import com.wch.gulimall.product.entity.AttrEntity;
import com.wch.gulimall.product.service.AttrService;
import com.wch.gulimall.product.vo.AttrGroupWithAttrsVo;
import com.wch.gulimall.product.vo.web.SkuItemVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wch.common.utils.PageUtils;
import com.wch.common.utils.Query;

import com.wch.gulimall.product.dao.AttrGroupDao;
import com.wch.gulimall.product.entity.AttrGroupEntity;
import com.wch.gulimall.product.service.AttrGroupService;
import org.springframework.util.StringUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrService attrService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 选择一个三级分类，查出他的属性
     * @param params
     * @param categoryId
     * @return
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params, Long categoryId) {
        String key = (String) params.get("key");
        QueryWrapper<AttrGroupEntity> queryWrapper =
                new QueryWrapper<>();
        if (!StringUtils.isEmpty(key)){
            queryWrapper.and(obj -> obj.eq("attr_group_id", key).or().like("attr_group_name", key));
        }
        if (categoryId == 0){
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    queryWrapper);
            return new PageUtils(page);
        }else {
           queryWrapper.eq("catelog_id", categoryId);
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    queryWrapper);
            return new PageUtils(page);
        }
    }

    /**
     * 根据某个三级分类id，获取他的所有属性分组，并且查出每个属性分组下面的属性
     * @param catelogId
     * @return
     */
    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsById(Long catelogId) {
        //查询分组信息
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        return attrGroupEntities.stream().map(group -> {
            AttrGroupWithAttrsVo attrGroupWithAttrsVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(group, attrGroupWithAttrsVo);
            List<AttrEntity> relationAttrInfo = attrService.getRelationAttrInfo(attrGroupWithAttrsVo.getAttrGroupId());
            attrGroupWithAttrsVo.setAttrs(relationAttrInfo);
            return attrGroupWithAttrsVo;
        }).collect(Collectors.toList());
    }

    /**
     * 查出当前spu对应的所有属性的分组信息以及当前分组下的所有属性对应的值
     * @param spuId
     * @param catelogId
     * @return
     */
    @Override
    public List<SkuItemVo.SpuItemBaseAttrVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catelogId) {
       return baseMapper.getAttrGroupWithAttrsBySpuId(spuId, catelogId);
    }

}