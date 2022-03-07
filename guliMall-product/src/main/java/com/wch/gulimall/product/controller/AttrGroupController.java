package com.wch.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.wch.gulimall.product.entity.AttrEntity;
import com.wch.gulimall.product.service.AttrAttrgroupRelationService;
import com.wch.gulimall.product.service.AttrService;
import com.wch.gulimall.product.service.CategoryService;
import com.wch.gulimall.product.vo.AttrGroupVo;
import com.wch.gulimall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.wch.gulimall.product.entity.AttrGroupEntity;
import com.wch.gulimall.product.service.AttrGroupService;
import com.wch.common.utils.PageUtils;
import com.wch.common.utils.R;



/**
 * 属性分组
 *
 * @author wch
 * @email 2879924329@qq.com
 * @date 2022-02-20 20:41:05
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    /**
     * 增加关联关系
     * @param
     * @return
     */
    @PostMapping("/attr/relation")
    public R addAttrRelationGroup(@RequestBody List<AttrGroupVo> attrGroupVos){
         attrAttrgroupRelationService.saveBatch(attrGroupVos);
        return R.ok();
    }

    /**
     * 根据分组id获取关联的信息
     * @param attrgroupId
     * @return
     */
    @GetMapping("/{attrgroupId}/attr/relation")
    public R getAttrRelationGroup(@PathVariable("attrgroupId") Long attrgroupId){
        List<AttrEntity> attrEntities = attrService.getRelationAttrInfo(attrgroupId);
        return R.ok().put("data", attrEntities);
    }
    /**
     * 根据分组id获取没有关联的信息
     * @param attrgroupId 分组id
     * @return
     */
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R getAttrNoRelationGroup(@PathVariable("attrgroupId") Long attrgroupId,
                                    @RequestParam Map<String, Object> params){
        PageUtils pageUtils = attrService.getRelationNoAttrInfo(params ,attrgroupId);
        return R.ok().put("page", pageUtils);
    }

    /**
     * 根据某个三级分类id，获取他的所有属性分组，并且查出每个属性分组下面的属性
     * @param catelogId
     * @return
     */
    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttrs(@PathVariable("catelogId") Long catelogId){
        List<AttrGroupWithAttrsVo> attrGroupWithAttrsVos = attrGroupService.getAttrGroupWithAttrsById(catelogId);
        return R.ok().put("data", attrGroupWithAttrsVos);
    }

    /**
     * 列表
     */
    @RequestMapping("/list/{categoryId}")
   // @RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params, @PathVariable Long categoryId){
    /*    PageUtils page = attrGroupService.queryPage(params);*/
        PageUtils page = attrGroupService.queryPage(params, categoryId);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long catelogId = attrGroup.getCatelogId();
        //获取完整的分类id路径 比如[22,223,2342]
        Long[] path = categoryService.findCateLogPath(catelogId);
        attrGroup.setCatelogPath(path);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 属性关联移除
     * @param attrGroupVos
     * @return
     */
    @PostMapping("/attr/relation/delete")
    public R deleteAttrRelation(@RequestBody AttrGroupVo[] attrGroupVos){
        attrService.deleteRelation(attrGroupVos);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
