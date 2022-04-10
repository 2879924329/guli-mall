package com.wch.gulimall.secondkill.scheduled;

import com.wch.gulimall.secondkill.service.SecondKillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/10 21:28
 *
 * 秒杀商品定时上架
 *
 * 每天晚上三点：上架最近三天需要秒杀的商品
 *
 */
@Service
public class SecondKillScheduled {


    @Autowired
    private SecondKillService secondKillService;

    @Scheduled(cron = "0 0 3 * * ?")
    public void upSecondKill(){
        //1，重复上架无需处理
        secondKillService.upSecondKill();
    }
}
