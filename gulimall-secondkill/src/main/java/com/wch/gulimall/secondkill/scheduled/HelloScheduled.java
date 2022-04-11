package com.wch.gulimall.secondkill.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/10 21:11
 * @EnableScheduling ： 开启定时任务
 * @EnableAsync : 开启异步任务
 */
@Slf4j
@Component
/*@EnableAsync
@EnableScheduling*/
public class HelloScheduled {

    /**
     * 定时任务不应该阻塞，默认是阻塞的
     *   1）使用异步的方式提交线程池
     *   2）支持任务线程池,设置TaskSchedulingProperties task:scheduling:pool:size:5(不好使)
     *   3)异步任务     @Async
     *
     * 使用异步+定时任务解决定时任务不阻塞
     */
    //@Async
   // @Scheduled(cron = "* * * * * ?")
    public void hello(){
        log.info("hello");
    }
}
