package com.wch.gulimall.thirdparty.service;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/22 18:52
 */
public interface SmsService {
    /**
     * 发送短信
     * @param phone
     * @return
     */
    boolean send(String phone);
}
