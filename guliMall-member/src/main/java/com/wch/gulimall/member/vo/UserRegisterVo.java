package com.wch.gulimall.member.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/22 20:38
 */
@Data
public class UserRegisterVo {

    private String userName;


    private String password;


    private String phone;


    private String code;
}
