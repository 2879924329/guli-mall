package com.wch.gulimall.cart.interceptor;

import com.wch.common.constant.AuthServerConstant;
import com.wch.common.constant.CartConstant;
import com.wch.common.to.GiteeUser;
import com.wch.gulimall.cart.vo.UserInfoTo;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;


/**
 * @author wch
 * @version 1.0
 * @date 2022/3/26 20:15
 * 拦截器， 判断用户的登录情况，封装传递给controller
 */

public class CartInterceptor implements HandlerInterceptor {

    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();

    /**
     * 目标方法执行之前拦截
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfoTo userInfoTo = new UserInfoTo();
        HttpSession session = request.getSession();
        GiteeUser user = (GiteeUser) session.getAttribute(AuthServerConstant.LOGIN_USER);

        if (!StringUtils.isEmpty(user)) {
            //用户登录
            userInfoTo.setUserId(user.getId());
        }
        Cookie[] cookies = request.getCookies();
        if (!ArrayUtils.isEmpty(cookies)) {
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if (name.equals(CartConstant.TEMP_USER_COOKIE_NAME)) {
                    //user-key
                    userInfoTo.setUserKey(cookie.getValue());
                    userInfoTo.setTempUser(true);
                }
            }
        }
        //如果木有临时用户一定分配一个临时用户
        if (StringUtils.isEmpty(userInfoTo.getUserKey())) {
            String userKey = UUID.randomUUID().toString();
            userInfoTo.setUserKey(userKey);
        }
        //目标方法放行之前，存储用户信息
        threadLocal.set(userInfoTo);
        return true;
    }

    /**
     * 业务执行之后, 分配临时用户，让浏览器保存临时用户的cookie信息
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //保存临时用户的cookie
        UserInfoTo userInfoTo = threadLocal.get();

        if (!userInfoTo.isTempUser()) {
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userInfoTo.getUserKey());
            cookie.setDomain("guli-mall.com");
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
            response.addCookie(cookie);
        }
        threadLocal.remove();
    }
}
