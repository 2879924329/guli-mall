package com.wch.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wch.common.utils.PageUtils;
import com.wch.gulimall.member.entity.MemberEntity;
import com.wch.gulimall.member.exception.PhoneExistException;
import com.wch.gulimall.member.exception.UserExistException;
import com.wch.gulimall.member.vo.UserRegisterVo;

import java.util.Map;

/**
 * 会员
 *
 * @author wch
 * @email 2879924329@qq.com
 * @date 2022-02-20 22:07:06
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(UserRegisterVo userRegisterVo);

   void checkUserName(String userName) throws UserExistException;

   void checkPhone(String phone) throws PhoneExistException;
}

