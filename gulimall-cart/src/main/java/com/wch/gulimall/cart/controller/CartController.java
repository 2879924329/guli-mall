package com.wch.gulimall.cart.controller;

import com.wch.common.constant.AuthServerConstant;
import com.wch.gulimall.cart.interceptor.CartInterceptor;
import com.wch.gulimall.cart.vo.UserInfoTo;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/26 20:07
 */
@Controller
public class CartController {

    /**
     * 浏览器有一个cookie：user-key， 标识用户身份，一个月后过期
     * 如果第一次使用购物车功能，都会自动给一个临时的用户身份
     * 浏览器以后保存，每次访问都带上这个cookie
     * @return
     */
    @GetMapping("/cart.html")
    public String cartPage(){
        //快速得到用户信息
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        System.out.println(userInfoTo);
        return "cartList";
    }

    /**
     * 添加购物车
     * @return
     */
    @GetMapping("/addToCart")
    public String addToCart(){
        return "success";
    }
}
