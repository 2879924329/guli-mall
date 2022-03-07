package com.wch.gulimall.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author WCH
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GuliMallCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliMallCouponApplication.class, args);
    }

}
