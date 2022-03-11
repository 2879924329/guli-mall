package com.wch.gulimall.warehouse.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.wch.gulimall.warehouse.vo.SkuHasStockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.wch.gulimall.warehouse.entity.WareSkuEntity;
import com.wch.gulimall.warehouse.service.WareSkuService;
import com.wch.common.utils.PageUtils;
import com.wch.common.utils.R;



/**
 * 商品库存
 *
 * @author wch
 * @email 2879924329@qq.com
 * @date 2022-02-20 22:27:21
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("ware:waresku:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 获取库存
     * @param skuIds
     * @return
     */
    @PostMapping("/hasstock")
    public R getSkuStock(@RequestBody List<Long> skuIds){
        List<SkuHasStockVo> hasStockVos = wareSkuService.getSkuStock(skuIds);
        return R.ok().setData(hasStockVos);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:waresku:info")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("ware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:waresku:delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
