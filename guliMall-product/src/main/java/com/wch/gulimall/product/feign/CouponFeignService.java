package com.wch.gulimall.product.feign;

import com.wch.common.to.SkuReductionTo;
import com.wch.common.to.SpuBoundsTo;
import com.wch.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/5 20:55
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService {
    /**
     * 远程调用 存积分
     * @param spuBoundsTo spuBoundsTo
     *
     *  1, couponFeignService.saveSpuBounds(spuBoundsTo)
     *     1),  @RequestBody:将这个对象转为json
     *     2), 找到 gulimall-coupon这个服务， 给/coupon/spubounds/save发送请求
     *           将上一步转的json放在请求体位置，发送请求
     *     3), 给对方服务发送请求，请求体里面有json数据
     *         （ @RequestBody SpuBoundsEntity spuBounds）：将请求体的json转为SpuBoundsEntity
     *        只要json的数据模型是兼容的，双方服务无需使用同一个to
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundsTo spuBoundsTo);

    /**
     * 远程调用， 满减处理
     * @param skuReductionTo
     * @return
     */
    @PostMapping("/coupon/skufullreduction/saveInfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
