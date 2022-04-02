package com.wch.gulimall.order.feign;

import com.wch.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/1 21:58
 */
@FeignClient("gulimall-warehouse")
public interface WaresFeignService {
    /**
     * 获取库存
     * @param skuIds
     * @return
     */
    @PostMapping("/ware/waresku/hasstock")
     R getSkuStock(@RequestBody List<Long> skuIds);
}
