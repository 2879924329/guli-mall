package com.wch.gulimall.member.vo;

import com.wch.gulimall.member.entity.MemberEntity;
import lombok.Data;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/23 19:36
 */
@Data
public class MemberLoginVo {
    private String loginAccount;
    private String password;
}
