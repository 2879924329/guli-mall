package com.wch.gulimall.cart.feign;

import com.wch.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/27 20:23
 *
 */
@FeignClient("gulimall-product")
public interface ProductFeignService {
    /**
     * 信息
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    //@RequiresPermissions("product:skuinfo:info")
    R info(@PathVariable("skuId") Long skuId);

    @GetMapping("/product/skusaleattrvalue/stringlist/{skuId}")
    List<String> getSkuSaleAttrValues(@PathVariable("skuId")Long skuId);

    @GetMapping("/product/skuinfo/{skuId}/getPrice")
    R getPrice(@PathVariable("skuId") Long skuId);
}
