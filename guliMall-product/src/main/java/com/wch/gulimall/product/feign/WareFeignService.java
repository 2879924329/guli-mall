package com.wch.gulimall.product.feign;

import com.wch.common.to.SkuHasStockTo;
import com.wch.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/9 21:55
 */
@FeignClient("gulimall-warehouse")
public interface WareFeignService {
    /**
     * 远程调用查询库存
     * @param skuIds
     * @return
     */
    @PostMapping("/ware/waresku/hasstock")
    public R getSkuStock(@RequestBody List<Long> skuIds);
}
