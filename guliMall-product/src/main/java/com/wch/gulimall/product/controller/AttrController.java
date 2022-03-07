package com.wch.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.wch.gulimall.product.entity.ProductAttrValueEntity;
import com.wch.gulimall.product.service.ProductAttrValueService;
import com.wch.gulimall.product.vo.AttrResponseVo;
import com.wch.gulimall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.wch.gulimall.product.entity.AttrEntity;
import com.wch.gulimall.product.service.AttrService;
import com.wch.common.utils.PageUtils;
import com.wch.common.utils.R;



/**
 * 商品属性
 *
 * @author wch
 * @email 2879924329@qq.com
 * @date 2022-02-20 20:41:05
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @GetMapping("/{attrType}/list/{catelogId}")
    public R baseAttrList(@RequestParam Map<String, Object> params,
                          @PathVariable("catelogId") Long catelogId,
                          @PathVariable("attrType") String attrType){
        PageUtils page = attrService.queryBaseAttr(params, catelogId, attrType);

        return R.ok().put("page", page);
    }

    /**
     * 商品规格维护数据查询回显
     * @param spuId
     * @return
     */
    @GetMapping("/base/listforspu/{spuId}")
    public R getBaseAttrValues(@PathVariable("spuId") Long spuId){
        List<ProductAttrValueEntity> entities = productAttrValueService.getBaseAttrValues(spuId);
        return R.ok().put("data", entities);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
        AttrResponseVo attr = attrService.getAttrInfo(attrId);

        return R.ok().put("attr", attr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attr){
		attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrResponseVo attr){
		attrService.updateAttr(attr);

        return R.ok();
    }
    @PostMapping("/update/{spuId}")
    // @RequiresPermissions("product:attr:update")
    public R updateBaseAttValue(@PathVariable("spuId") Long spuId,
                                @RequestBody List<ProductAttrValueEntity> entities){
      productAttrValueService.updateSpuAttr(spuId, entities);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
