package com.wch.gulimall.authserver.vo;

import lombok.Data;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/23 19:27
 */
@Data
public class UserLoginVo {
    private String loginAccount;
    private String password;
}
