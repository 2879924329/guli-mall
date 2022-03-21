package com.wch.gulimall.authserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/20 21:45
 *
 * 试图映射
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     *  @GetMapping("/login.html")
     *     public String login(){
     *         return "login";
     *     }
     *
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
       registry.addViewController("/login.html").setViewName("login");
       registry.addViewController("/reg.html").setViewName("reg");
    }
}
