package com.wch.gulimall.order.feign;

import com.wch.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/2 22:19
 */
@FeignClient("gulimall-product")
public interface ProductFeignService {
    @GetMapping("/product/spuinfo/skuId/{id}")
   R getSpuInfo(@PathVariable("id") Long skuId);
}
