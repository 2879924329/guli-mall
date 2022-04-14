package com.wch.gulimall.product.feign.fallback;

import com.wch.common.exception.Code;
import com.wch.common.utils.R;
import com.wch.gulimall.product.feign.SecKillFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/14 21:10
 */
@Slf4j
@Component
public class SecKillFeignServiceFallBack implements SecKillFeignService {
    @Override
    public R getSecKillSkuInfo(Long skuId) {
        log.info("熔断调用！！！");
        return R.error(Code.TO_MANY_REQUEST.getCode(), Code.TO_MANY_REQUEST.getMessage());
    }
}
