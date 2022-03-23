package com.wch.gulimall.authserver.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/22 19:43
 *
 * 注册
 */
@Data
public class UserRegisterVo {

    @NotEmpty(message = "用户名必须提交")
    @Length(min = 6, max = 18, message = "用户名必须再6-18位")
    private String userName;

    @NotEmpty(message = "密码不能为空")
    @Length(min = 6, max = 18, message = "密码必须再6-18位")
    private String password;

    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$", message = "手机号格式不正确")
    @NotEmpty(message = "手机号不能为空")
    private String phone;

    @NotEmpty(message = "验证码不能为空")
    private String code;
}
