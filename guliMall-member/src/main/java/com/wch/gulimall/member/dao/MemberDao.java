package com.wch.gulimall.member.dao;

import com.wch.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author wch
 * @email 2879924329@qq.com
 * @date 2022-02-20 22:07:06
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
