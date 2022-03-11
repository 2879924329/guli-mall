package com.wch.gulimall.product.feign;

import com.wch.common.to.SkuEsModel;
import com.wch.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/9 22:42
 */
@FeignClient("gulimall-search")
public interface SearchFeignService {
    /**
     * 远程调用保存商品到es
     * @param skuEsModels
     * @return
     */
    @RequestMapping("/search/save/product")
    R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);
}
