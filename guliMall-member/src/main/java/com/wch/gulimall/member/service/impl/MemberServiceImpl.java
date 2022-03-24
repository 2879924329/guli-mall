package com.wch.gulimall.member.service.impl;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.wch.gulimall.member.exception.PhoneExistException;
import com.wch.gulimall.member.exception.UserExistException;
import com.wch.gulimall.member.vo.MemberLoginVo;
import com.wch.gulimall.member.vo.UserRegisterVo;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wch.common.utils.PageUtils;
import com.wch.common.utils.Query;

import com.wch.gulimall.member.dao.MemberDao;
import com.wch.gulimall.member.entity.MemberEntity;
import com.wch.gulimall.member.service.MemberService;
import org.springframework.util.StringUtils;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {


    @Autowired
    private MemberDao memberDao;

    @Autowired
    private MemberService memberService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 会员注册
     * @param userRegisterVo
     */
    @Override
    public void register(UserRegisterVo userRegisterVo) {
        MemberEntity memberEntity = new MemberEntity();

        //设置默认等级
        MemberEntity levelEntity = memberDao.getDefaultLevel();
        memberEntity.setLevelId(levelEntity.getId());

        //检查用户名和手机号是否唯一,为了能让controller可以感知异常, 使用异常处理
        checkPhone(userRegisterVo.getPhone());
        checkUserName(userRegisterVo.getUserName());
        memberEntity.setMobile(userRegisterVo.getPhone());
        memberEntity.setUsername(userRegisterVo.getUserName());

        //DigestUtils.md5()
        //密码加密存储, md5不能直接加密进行存储，要盐值加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = passwordEncoder.encode(userRegisterVo.getPassword());
        memberEntity.setPassword(password);
        baseMapper.insert(memberEntity);
    }

    @Override
    public void checkUserName(String userName) {
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName));
        if (count > 0){
            throw new UserExistException();
        }
    }

    @Override
    public void checkPhone(String phone) {
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (count > 0){
            throw new PhoneExistException();
        }
    }

    /**
     * 登录
     * @param memberLoginVo
     * @return
     */
    @Override
    public MemberEntity login(MemberLoginVo memberLoginVo) {
        String loginAccount = memberLoginVo.getLoginAccount();
        String password = memberLoginVo.getPassword();
        //去数据库查询
        MemberEntity member = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("username", loginAccount)
                .or().eq("mobile", loginAccount));
        if (StringUtils.isEmpty(member)){
            //登录失败
            return null;
        }else {
            //比较密码
            //获取到数据库的password
            String passwordDB = member.getPassword();
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            //密码匹配
            boolean matches = bCryptPasswordEncoder.matches(password, passwordDB);
            if (matches){
                return member;
            }else {
                //密码匹配失败
                return null;
            }
        }
    }

}