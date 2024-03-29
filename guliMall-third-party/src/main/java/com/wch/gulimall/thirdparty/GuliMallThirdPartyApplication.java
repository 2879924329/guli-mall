package com.wch.gulimall.thirdparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author WCH
 */
@EnableDiscoveryClient
@SpringBootApplication
public class GuliMallThirdPartyApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliMallThirdPartyApplication.class, args);
    }

}
