package com.wch.gulimall.product.service.impl;

import com.wch.common.constant.ProductConstant;
import com.wch.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.wch.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.wch.gulimall.product.entity.AttrGroupEntity;
import com.wch.gulimall.product.entity.CategoryEntity;
import com.wch.gulimall.product.service.AttrAttrgroupRelationService;
import com.wch.gulimall.product.service.AttrGroupService;
import com.wch.gulimall.product.service.CategoryService;
import com.wch.gulimall.product.vo.AttrGroupVo;
import com.wch.gulimall.product.vo.AttrResponseVo;
import com.wch.gulimall.product.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wch.common.utils.PageUtils;
import com.wch.common.utils.Query;

import com.wch.gulimall.product.dao.AttrDao;
import com.wch.gulimall.product.entity.AttrEntity;
import com.wch.gulimall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {


    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        //保存基本数据
        this.save(attrEntity);
        //保存关联关系, 基本属性才需要保存分组的事情，销售属性不需要
        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() && attr.getAttrGroupId() != null){
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationService.save(attrAttrgroupRelationEntity);
        }


    }

    @Override
    public PageUtils queryBaseAttr(Map<String, Object> params, Long catelogId, String attrType) {
        QueryWrapper<AttrEntity> attrEntityQueryWrapper =
                new QueryWrapper<AttrEntity>()
                        .eq("attr_type",
                                "base".equalsIgnoreCase(attrType) ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
        if (catelogId != 0){
            attrEntityQueryWrapper.eq("catelog_id", catelogId);
        }
        String  key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            attrEntityQueryWrapper.and((attrEntityQueryWrapper1
                    -> attrEntityQueryWrapper1.eq("attr_id", key).or().like("attr_name", key)));
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),attrEntityQueryWrapper

        );

        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
        List<AttrResponseVo> attrResponseVoList = records.stream().map(attrEntity -> {
            AttrResponseVo attrResponseVo = new AttrResponseVo();
            BeanUtils.copyProperties(attrEntity, attrResponseVo);
            if ("base".equalsIgnoreCase(attrType)){
                //设置分类和分组的名字
                AttrAttrgroupRelationEntity attrAttrgroupRelationEntity =
                        attrAttrgroupRelationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>()
                                .eq("attr_id", attrEntity.getAttrId()));
                if (attrAttrgroupRelationEntity != null && attrAttrgroupRelationEntity.getAttrGroupId() != null) {
                    AttrGroupEntity groupEntity = attrGroupService.getById(attrAttrgroupRelationEntity.getAttrGroupId());
                    attrResponseVo.setGroupName(groupEntity.getAttrGroupName());
                }
            }
            CategoryEntity categoryEntity = categoryService.getById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                attrResponseVo.setCatelogName(categoryEntity.getName());
            }
            return attrResponseVo;
        }).collect(Collectors.toList());
         pageUtils.setList(attrResponseVoList);
        return pageUtils;
    }

    @Override
    public AttrResponseVo getAttrInfo(Long attrId) {
        AttrResponseVo attrResponseVo = new AttrResponseVo();
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity, attrResponseVo);
        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            //设置分组信息
            AttrAttrgroupRelationEntity attrgroupRelationEntity = attrAttrgroupRelationService
                    .getOne(new QueryWrapper<AttrAttrgroupRelationEntity>()
                            .eq("attr_id", attrId));
            if (attrgroupRelationEntity != null){
                attrResponseVo.setAttrGroupId(attrgroupRelationEntity.getAttrGroupId());
                AttrGroupEntity attrGroupEntity = attrGroupService.getById(attrgroupRelationEntity.getAttrGroupId());
                if (attrGroupEntity != null){
                    attrResponseVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }
       //设置分类信息
        Long catelogId = attrEntity.getCatelogId();
        Long[] cateLogPath = categoryService.findCateLogPath(catelogId);
        attrResponseVo.setCatelogPath(cateLogPath);
        CategoryEntity categoryEntity = categoryService.getById(catelogId);
        if (categoryEntity != null){
            attrResponseVo.setCatelogName(categoryEntity.getName());
        }
        return attrResponseVo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateAttr(AttrResponseVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.updateById(attrEntity);
        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            //修改分组关联
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
            attrAttrgroupRelationEntity.setAttrId(attr.getAttrId());
            //判断原来是否具有分组
            int count = attrAttrgroupRelationService.count(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            if (count > 0){
                attrAttrgroupRelationService.update(attrAttrgroupRelationEntity,
                        new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));

            }else {
                attrAttrgroupRelationService.save(attrAttrgroupRelationEntity);
            }
        }
    }

    /**
     * 根据分组id查找关联的所有属性
     * @param attrgroupId 分组id
     * @return
     */
    @Override
    public List<AttrEntity> getRelationAttrInfo(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> attrgroupRelationEntities = attrAttrgroupRelationService.list(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));
        List<Long> attrIds = attrgroupRelationEntities.stream().map(attr -> attr.getAttrId()).collect(Collectors.toList());
       if (attrIds.isEmpty()){
           return Collections.emptyList();
       }
        return this.listByIds(attrIds);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteRelation(AttrGroupVo[] attrGroupVos) {
        List<AttrAttrgroupRelationEntity> entityList = Arrays.asList(attrGroupVos).stream().map(item -> {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, attrAttrgroupRelationEntity);
            return attrAttrgroupRelationEntity;
        }).collect(Collectors.toList());
        attrAttrgroupRelationDao.deleteBatch(entityList);
    }

    /**
     * 获取当前分组没有关联的所有属性
     * @param params 分页参数
     * @param attrgroupId 分组id
     * @return
     */
    @Override
    public PageUtils getRelationNoAttrInfo(Map<String, Object> params, Long attrgroupId) {
       //1,当前分组只能关联自己所属分类里面的属性
        AttrGroupEntity attrGroupEntity = attrGroupService.getById(attrgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();
        //2,当前分组只能关联别的分组没有引用的属性
          //2.1 当前分类下的其他分组
        List<AttrGroupEntity> groupEntities = attrGroupService.list(new QueryWrapper<AttrGroupEntity>()
                .eq("catelog_id", catelogId));
        List<Long> collect = groupEntities.stream().map(item -> item.getAttrGroupId()).collect(Collectors.toList());
        //2.2 这些分组关联的属性
        List<AttrAttrgroupRelationEntity> groupId = attrAttrgroupRelationService.list(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", collect));
        List<Long> attrIds = groupId.stream().map(item -> item.getAttrId()).collect(Collectors.toList());
        //2.3 从当前的所有属性中剔除这些属性
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>()
                .eq("catelog_id", catelogId)
                .eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if (!attrIds.isEmpty()){
            queryWrapper.notIn("attr_id", attrIds);
        }
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            queryWrapper.and(w -> w.eq("attr_id", key).or().like("attr_name", key));
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }

    /**
     * 在指定的所有属性集合里面，挑出检索属性
     * @param attrIdCollect
     * @return
     */
    @Override
    public List<Long> selectSearchAttrs(List<Long> attrIdCollect) {
        return this.baseMapper.selectSearchAttr(attrIdCollect);
    }

}