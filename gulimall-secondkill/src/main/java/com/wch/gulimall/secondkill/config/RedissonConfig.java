package com.wch.gulimall.secondkill.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/13 22:23
 * <p>
 * 学习redis配置类
 */
@Configuration
public class RedissonConfig {
    /**
     * 所有对redisson使用都是通过RedissonClient对象
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.84.88:6379");
        return Redisson.create(config);
    }
}
