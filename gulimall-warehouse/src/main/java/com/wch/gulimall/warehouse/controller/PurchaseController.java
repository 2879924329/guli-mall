package com.wch.gulimall.warehouse.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.wch.common.exception.Code;
import com.wch.gulimall.warehouse.vo.MergeVo;
import com.wch.gulimall.warehouse.vo.PurchaseFinishVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.wch.gulimall.warehouse.entity.PurchaseEntity;
import com.wch.gulimall.warehouse.service.PurchaseService;
import com.wch.common.utils.PageUtils;
import com.wch.common.utils.R;



/**
 * 采购信息
 *
 * @author wch
 * @email 2879924329@qq.com
 * @date 2022-02-20 22:27:21
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 合并采购单
     * @return
     */
    @PostMapping("/merge")
    public R merge(@RequestBody MergeVo mergeVo){
        purchaseService.mergePurchase(mergeVo);
        return R.ok();
    }

    /**
     * 领取采购单
     * @param ids
     * @return
     */
    @PostMapping("/received")
    public R received(@RequestBody List<Long> ids ){
        purchaseService.received(ids);
        return R.ok();
    }

    /**
     * 完成采购单
     * @param purchaseFinishVo
     * @return
     */
    @PostMapping("/done")
    public R done(@RequestBody PurchaseFinishVo purchaseFinishVo){
        purchaseService.done(purchaseFinishVo);
        return R.ok();
    }


    /**
     * 获取未领取的采购单列表
     */
    @RequestMapping("/unreceived/list")
    // @RequiresPermissions("ware:purchase:list")
    public R unReceiveList(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryUnreceivePage(params);
        return R.ok().put("page", page);
    }

    /**
     * 分配采购单
     * @param purchase
     * @return
     */
    // /ware/purchase/assign
    @PostMapping("/assign")
    public R done(@RequestBody PurchaseEntity purchase) {
        boolean res = purchaseService.purchaseAssign(purchase);
        if (res) {
            return R.ok();
        }
        return R.error(Code.WARE_PURCHASE_ASSIGN_FAILED.getCode(),
                Code.WARE_PURCHASE_ASSIGN_FAILED.getMessage());
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("ware:purchase:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:purchase:info")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:purchase:save")
    public R save(@RequestBody PurchaseEntity purchase){
        purchase.setUpdateTime(new Date());
        purchase.setCreateTime(new Date());
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("ware:purchase:update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:purchase:delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
