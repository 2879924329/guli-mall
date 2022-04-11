package com.wch.gulimall.secondkill.feign;

import com.wch.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/11 19:32
 */
@FeignClient("gulimall-product")
public interface ProductFeignService {
    /**
     * 信息
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
   R getSkuInfo(@PathVariable("skuId") Long skuId);
}
