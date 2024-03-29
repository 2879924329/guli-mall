package com.wch.gulimall.product.feign;

import com.wch.common.utils.R;
import com.wch.gulimall.product.feign.fallback.SecKillFeignServiceFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/12 19:52
 */
@FeignClient(value = "gulimall-seckill", fallback = SecKillFeignServiceFallBack.class)
public interface SecKillFeignService {
    @GetMapping("/sku/seckill/{skuId}")
    R getSecKillSkuInfo(@PathVariable("skuId") Long skuId);
}

