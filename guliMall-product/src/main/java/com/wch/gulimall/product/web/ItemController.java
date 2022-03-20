package com.wch.gulimall.product.web;

import com.wch.gulimall.product.service.SkuInfoService;
import com.wch.gulimall.product.vo.web.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


/**
 * @author wch
 * @version 1.0
 * @date 2022/3/19 21:57
 */
@Controller
public class ItemController {


    @Autowired
    private SkuInfoService skuInfoService;
    /**
     * 展示当前的sku的详情
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId){
        System.out.println("准备查询" + skuId);
        SkuItemVo skuItemVo = skuInfoService.item(skuId);
        return "item";
    }
}
