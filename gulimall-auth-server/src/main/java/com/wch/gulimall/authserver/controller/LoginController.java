package com.wch.gulimall.authserver.controller;

import com.alibaba.fastjson.TypeReference;
import com.wch.common.constant.AuthServerConstant;
import com.wch.common.exception.Code;
import com.wch.common.utils.R;
import com.wch.common.utils.RandomUtils;
import com.wch.gulimall.authserver.feign.MemberFeignService;
import com.wch.gulimall.authserver.feign.ThirdPartService;
import com.wch.gulimall.authserver.vo.UserRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/20 21:09
 */
@Controller
public class LoginController {


    /**
     * 发送一个请求直接跳转一个页面，
     * 使用mvc viewController将请求直接映射
     */


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ThirdPartService thirdPartService;

    @Autowired
    private MemberFeignService memberFeignService;

    @ResponseBody
    @GetMapping("/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone) {
        //TODO 验证码防刷
        String key = AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone;
        String redisCode = stringRedisTemplate.opsForValue().get(key);
        if (!StringUtils.isEmpty(redisCode)) {
            long time = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - time < 60000){
                return R.error(Code.CODE_CACHE_EXISTS.getCode(), Code.CODE_CACHE_EXISTS.getMessage());
            }
        }
        //若Redis中没有，调用接口发送短信方法

        //验证码再次校验
        String code = RandomUtils.getSixBitRandom();
        //加上当前使时间戳，判断用户的发送验证码的时间
        String subString = code + "_" + System.currentTimeMillis();
        //redis缓存验证码，防止同一个手机号60s内再次发送验证码
        stringRedisTemplate.opsForValue().set(key, subString, 10, TimeUnit.MINUTES);
        thirdPartService.send(phone, code);
        return R.ok();
    }


    /**
     * 注册
     * <p>
     * 使用 return "forward:/reg.html"; 问题？Request method 'POST' not supported
     * 流程：用户注册 -> /register[Post] ->转发 /reg.html（路径映射莫热门都是get方式）
     * <p>
     * 重定向携带数据，利用session原理，将数据放在session中。只要跳到下一个页面取出这个数据后，session里面的数据就会被删除
     * //TODO 分布式下的session问题
     * RedirectAttributes :重定向视图，并且带上数据， model重定向不会携带数据
     *
     * @return
     */
    @PostMapping("/register")
    public String register(@Valid UserRegisterVo user, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            //有错转发到注册页
            /**
             * map(fieldError -> {
             *                 String field = fieldError.getField();
             *                 String defaultMessage = fieldError.getDefaultMessage();
             *                 stringHashMap.put(field, defaultMessage);
             *                 return
             *                 });
             */
            Map<String, String> errorMap = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, DefaultMessageSourceResolvable::getDefaultMessage));
            //model.addAttribute("errors", errorMap);
            redirectAttributes.addFlashAttribute("errors", errorMap);
            return "redirect:http://auth.guli-mall.com/reg.html";
        }

        //1）校验验证码
        String code = user.getCode();
        String phone = user.getPhone();
        String key = AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone;
        String redisCode = stringRedisTemplate.opsForValue().get(key);

        if (!StringUtils.isEmpty(redisCode)) {
            if (code.equals(redisCode.split("_")[0])) {
                //验证码通过
                // 调用远程服务进行注册
                //删除验证码
                stringRedisTemplate.delete(key);
                R register = memberFeignService.register(user);
                if (register.getCode() == 0){
                    //注册成功回到首页，回到登录页，
                    return "redirect:http://auth.guli-mall.com/login.html";
                }else {
                    //失败
                    Map<String, String> errorMap = new HashMap<>();
                    errorMap.put("msg", register.getData(new TypeReference<String>(){}));
                    redirectAttributes.addFlashAttribute("errors", errorMap);
                    return "redirect:http://auth.guli-mall.com/reg.html";

                }
            }else {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("code", "验证码错误");
                redirectAttributes.addFlashAttribute("errors", errorMap);
                return "redirect:http://auth.guli-mall.com/reg.html";
            }
        } else {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("code", "验证码错误");
            redirectAttributes.addFlashAttribute("errors", errorMap);
            return "redirect:http://auth.guli-mall.com/reg.html";
        }

    }
}
