package com.wch.gulimall.authserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/23 21:30
 *
 * gitee配置
 */

@Data
@Configuration
@ConfigurationProperties(prefix = "gitee.oauth")
public class GiteeLoginConfig {
    /**
     * gitee授权中提供的 appid
     */
    public String clientId;

    /**
     * appKey
     */
    public String clientSecret;

    /**
     * 回调地址
     */
    public String redirectUrl;
    /**
     * gitee授权code
     */
    private String code;
    /**
     * state
     */
    private String state;

}
