package com.wch.gulimall.search.feign;

import com.wch.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


/**
 * @author wch
 * @version 1.0
 * @date 2022/3/18 21:33
 */
@FeignClient("gulimall-product")
public interface ProductFeignService {

    /**
     * 信息
     */
    @RequestMapping("/product/attr/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId);


    @GetMapping("/product/attr/infos")
    public R brandInfo(@RequestParam("brandIds") List<Long> brandIds);
}
