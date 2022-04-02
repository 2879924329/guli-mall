package com.wch.gulimall.order.interceptor;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/1 19:51
 *
 * feign远程调用拦截器，拒绝远程调用丢失原请求携带的参数
 */
@Configuration
public class GuliFeignInterceptor {
    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor(){
        return requestTemplate -> {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
             if(!ObjectUtils.isEmpty(requestAttributes)){
                 HttpServletRequest request = requestAttributes.getRequest();
                 if (!ObjectUtils.isEmpty(request)){
                     //同步请求头数据
                     String cookie = request.getHeader("Cookie");
                     //给新请求同步老请求的cookie
                     requestTemplate.header("Cookie",cookie);
                 }
             }
        };
    }
}
