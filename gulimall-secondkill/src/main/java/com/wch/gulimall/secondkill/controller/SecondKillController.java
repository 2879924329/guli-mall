package com.wch.gulimall.secondkill.controller;

import com.wch.common.utils.R;
import com.wch.gulimall.secondkill.service.SecondKillService;
import com.wch.gulimall.secondkill.to.SecondKillRedisEntityTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/11 21:25
 */
@RestController
public class SecondKillController {


    @Autowired
    private SecondKillService secondKillService;

    /**
     * 获取当前时间可以参与秒杀的商品
     * @return
     */
    @GetMapping("/current-secondkills")
    public R getCurrentSecondKills(){
        List<SecondKillRedisEntityTo> secondKillRedisEntityTos = secondKillService.queryCurrentSecondKillSkus();
        return R.ok().setData(secondKillRedisEntityTos);
    }
}
