package com.wch.gulimall.warehouse.feign;

import com.wch.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/7 15:58
 */
@FeignClient("gulimall-product")
public interface ProductFeignService {
    /**
     * 信息
     *
     * 接口的两种写法
     *  1，让所有请求过网关
     *      1）， @FeignClient("gulimall-gateway") 给gulimall-gateway所在的机器发送请求
     *      2）， /api/product/skuinfo/info/{skuId}
     * 2,直接给后他服务器发送服务处理
     *     1），@FeignClient("gulimall-product")
     *     2），/product/skuinfo/info/{skuId}
     * @param skuId sku id
     * @return R
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);
}
