package com.wch.common.to;

import lombok.Data;

import java.util.Date;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/24 22:12
 */
@Data
public class UserResponseTo {

    /**
     * 社交平台uid
     */
    private String socialUid;
    /**
     * 社交平台给的访问令牌
     */
    private String accessToken;
    /**
     * 当次令牌的过期时间，单位s
     */
    private Integer expireIn;
    /**
     * 注册类型[0-本平台，1-微博，2-微信]
     */
    private Integer registerType;
    /**
     * id
     */
    private Long id;
    /**
     * 会员等级id
     */
    private Long levelId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 昵称
     */
    private String name;
    /**
     * 手机号码
     */
    private String mobile;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 头像
     */
    private String header;
    /**
     * 性别[1-男，0-女]
     */
    private Integer gender;
    /**
     * 生日
     */
    private Date birth;
    /**
     * 所在城市
     */
    private String city;
    /**
     * 职业
     */
    private String job;
    /**
     * 个性签名
     */
    private String sign;
    /**
     * 用户来源
     */
    private Integer sourceType;
    /**
     * 积分
     */
    private Integer integration;
    /**
     * 成长值
     */
    private Integer growth;
    /**
     * 启用状态[0启用，1-禁用]
     */
    private Integer status;
    /**
     * 注册时间
     */
    private Date createTime;

}
