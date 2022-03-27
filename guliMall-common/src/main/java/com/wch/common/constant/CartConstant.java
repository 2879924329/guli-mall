package com.wch.common.constant;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/26 20:20
 */
public class CartConstant {
    public static final String TEMP_USER_COOKIE_NAME = "user-key";

    /**
     * 临时用户的cookie过期时间, 单位：s
     */
    public static final int TEMP_USER_COOKIE_TIMEOUT = 60 * 60 * 24 * 30;

    public static final String CART_PREFIX = "gulimall:cart";
}
