package com.wch.gulimall.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.wch.common.exception.Code;
import com.wch.gulimall.member.exception.PhoneExistException;
import com.wch.gulimall.member.exception.UserExistException;
import com.wch.gulimall.member.feign.CouponFeignService;
import com.wch.gulimall.member.vo.MemberLoginVo;
import com.wch.gulimall.member.vo.UserRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.wch.gulimall.member.entity.MemberEntity;
import com.wch.gulimall.member.service.MemberService;
import com.wch.common.utils.PageUtils;
import com.wch.common.utils.R;



/**
 * 会员
 *
 * @author wch
 * @email 2879924329@qq.com
 * @date 2022-02-20 22:07:06
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private CouponFeignService couponFeignService;


    /**
     * 获取会员得优惠券信息
     * @return
     */
    @RequestMapping("/coupons")
    public R getMemberCoupons(){

        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("测试数据-张三");
        R memberCoupons = couponFeignService.getMemberCoupons();
        return R.ok().put("member", memberEntity).put("memberCoupons", memberCoupons.get("coupons"));
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 会员注册
     * @return
     */
    @PostMapping("/register")
    public R register(@RequestBody UserRegisterVo userRegisterVo){
        try {
            memberService.register(userRegisterVo);
        }catch (UserExistException e){
            return R.error(Code.USER_EXIST_EXCEPTION.getCode(), Code.USER_EXIST_EXCEPTION.getMessage());
        }catch (PhoneExistException e){
            return R.error(Code.PHONE_EXIST_EXCEPTION.getCode(), Code.PHONE_EXIST_EXCEPTION.getMessage());
        }
        return R.ok();
    }

    /**
     * 登录
     * @param memberLoginVo
     * @return
     */
    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo memberLoginVo){
       MemberEntity member = memberService.login(memberLoginVo);
       if (!StringUtils.isEmpty(member)){
           return R.ok();
       }else {
           return R.error(Code.LOGIN_ACCOUNT_PASSWORD_INVALID_EXCEPTION.getCode(), Code.LOGIN_ACCOUNT_PASSWORD_INVALID_EXCEPTION.getMessage());
       }
    }
}
