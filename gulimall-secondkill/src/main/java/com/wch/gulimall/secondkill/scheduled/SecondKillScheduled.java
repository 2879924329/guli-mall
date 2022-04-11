package com.wch.gulimall.secondkill.scheduled;

import com.wch.gulimall.secondkill.service.SecondKillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

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
@Slf4j
@Service
public class SecondKillScheduled {

    @Autowired
    private SecondKillService secondKillService;

    @Autowired
    private RedissonClient redissonClient;

    private final String UP_LOCK = "seckill:up:lock";
    @Scheduled(cron = "*/5 * * * * ?")
    public void upSecondKill(){
        //1，幂等性处理，解决重复上架
        log.info("上架秒杀商品信息");
        //分布式锁，保证获取到锁的机器上架
        RLock lock = redissonClient.getLock(UP_LOCK);
        lock.lock(10, TimeUnit.SECONDS);
        try {
            secondKillService.upSecondKill();
        }finally {
            lock.unlock();
        }
    }
}
