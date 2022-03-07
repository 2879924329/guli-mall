package com.wch.gulimall.product.controller;

import java.util.Arrays;
import java.util.Map;

import com.wch.common.validator.group.AddGroup;
import com.wch.common.validator.group.UpdateGroup;
import com.wch.common.validator.group.UpdateStatusGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wch.gulimall.product.entity.BrandEntity;
import com.wch.gulimall.product.service.BrandService;
import com.wch.common.utils.PageUtils;
import com.wch.common.utils.R;


/**
 * 品牌
 *
 * @author wch
 * @email 2879924329@qq.com
 * @date 2022-02-20 20:41:05
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("product:brand:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    //@RequiresPermissions("product:brand:info")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:brand:save")
    public R save(@Validated({AddGroup.class}) @RequestBody BrandEntity brand/*, BindingResult result*/) {
        /*Map<String, String> map = new HashMap<>();
        if (result.hasErrors()) {
            //获取校验的结果
            result.getFieldErrors().forEach(item ->{
                //获取错误提示
                String defaultMessage = item.getDefaultMessage();
                //获取错误字段
                String field = item.getField();
                map.put(field, defaultMessage);
            });
          return R.error(400, "提交的数据不合法").put("data", map);
        } else{

        }*/

        brandService.save(brand);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("product:brand:update")
    public R update(@Validated({UpdateGroup.class}) @RequestBody BrandEntity brand){
        //修改品牌表的时候，要考虑其他表，比如品牌表和分类表的中间表中有品牌名等信息，所以要考虑一起修改
        //所有冗余存储的都要更新
		brandService.updateDetail(brand);
        return R.ok();
    }
    /**
     * 修改状态
     */
    @RequestMapping("/update/status")
    // @RequiresPermissions("product:brand:update")
    public R updateStatus(@Validated({UpdateStatusGroup.class}) @RequestBody BrandEntity brand){
        brandService.updateById(brand);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:brand:delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
