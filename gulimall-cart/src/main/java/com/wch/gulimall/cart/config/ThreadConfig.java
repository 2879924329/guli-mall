package com.wch.gulimall.cart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/20 20:11
 */
//@EnableConfigurationProperties(ThreadPoolProperties.class)
@Configuration
public class ThreadConfig {
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolProperties poolProperties) {
        return new ThreadPoolExecutor(poolProperties.getCorePoolSize(),
                poolProperties.getMaxPoolSize(), poolProperties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(100000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());

    }
}
