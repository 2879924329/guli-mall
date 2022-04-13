package com.wch.gulimall.secondkill.controller;

import com.wch.common.utils.R;
import com.wch.gulimall.secondkill.service.SecondKillService;
import com.wch.gulimall.secondkill.to.SecondKillRedisEntityTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/11 21:25
 */
@Controller
public class SecondKillController {


    @Autowired
    private SecondKillService secondKillService;

    /**
     * 获取当前时间可以参与秒杀的商品
     * @return
     */
    @ResponseBody
    @GetMapping("/current-secondkills")
    public R getCurrentSecondKills(){
        List<SecondKillRedisEntityTo> secondKillRedisEntityTos = secondKillService.queryCurrentSecondKillSkus();
        return R.ok().setData(secondKillRedisEntityTos);
    }

    /**
     * 商品详情页商品是否参与秒杀信息
     * @param skuId
     * @return
     */
    @ResponseBody
    @GetMapping("/sku/seckill/{skuId}")
    public R getSecKillSkuInfo(@PathVariable("skuId") Long skuId){
        SecondKillRedisEntityTo secondKillRedisEntityTo = secondKillService.querySecKillSkuInfo(skuId);
        return R.ok().setData(secondKillRedisEntityTo);
    }

    /**
     * 商品秒杀
     * @return
     */
    @GetMapping(value = "/kill")
    public String secondKillOrder(@RequestParam("killId") String killId,
                          @RequestParam("key") String key,
                          @RequestParam("num") Integer num,
                          Model model) {
        String orderSn;
        try {
            orderSn = secondKillService.secondKill(killId, key, num);
            model.addAttribute("orderSn", orderSn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }
}

