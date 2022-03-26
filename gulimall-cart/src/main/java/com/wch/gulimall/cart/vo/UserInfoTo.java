package com.wch.gulimall.cart.vo;

import lombok.Data;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/26 20:17
 */
@Data
public class UserInfoTo {
    private Long userId;
    private String userKey;

    /**
     * 临时用户标志位
     */
    private boolean tempUser = false;
}
