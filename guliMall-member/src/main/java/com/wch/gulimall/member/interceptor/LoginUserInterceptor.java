package com.wch.gulimall.member.interceptor;

import com.wch.common.constant.AuthServerConstant;
import com.wch.common.to.MemberEntityTo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/29 21:53
 */
@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberEntityTo> loginUser = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        //如果是请求的路径是当前路径，直接放行
        boolean match = new AntPathMatcher().match("/member/**", requestURI);
        if (match){
            return true;
        }

        MemberEntityTo attribute = (MemberEntityTo) request.getSession().getAttribute(AuthServerConstant.LOGIN_USER);
        if (!StringUtils.isEmpty(attribute)){
            loginUser.set(attribute);
            return true;
        }else {
            //木有登录就去登录
            request.getSession().setAttribute("msg", "请先登录");
            response.sendRedirect("http://auth.guli-mall.com/login.html");
            return false;
        }
    }

}
