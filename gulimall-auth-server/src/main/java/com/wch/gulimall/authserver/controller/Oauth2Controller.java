package com.wch.gulimall.authserver.controller;

import com.alibaba.fastjson.JSON;
import com.wch.common.constant.AuthServerConstant;
import com.wch.gulimall.authserver.config.GiteeLoginConfig;
import com.wch.gulimall.authserver.service.GiteeOauth2Service;
import com.wch.gulimall.authserver.vo.GiteeUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/23 22:24
 * <p>
 * 第三方登录
 */
@Controller
public class Oauth2Controller {

    @Autowired
    private GiteeOauth2Service giteeOauth2Service;

    @Autowired
    private GiteeLoginConfig giteeLoginConfig;

    /**
     * gitee第三方登录回调处理地址
     *
     * @return
     */
    @GetMapping("/oauth2/success")
    public String giteeLogin(@RequestParam(name = "code") String code,
                             @RequestParam(name = "state") String state, HttpSession session) {
        //使用GetUser方法将携带的accessToken链接发送至Gitee服务器获取用户的信息
        giteeLoginConfig.setState(state);
        giteeLoginConfig.setCode(code);
        //获取到accessToken
        String accessToken = giteeOauth2Service.getAccessToken(giteeLoginConfig);
        GiteeUser user = giteeOauth2Service.getUser(accessToken);
        //TODO 如果用户是第一次登录，自动注册。（为当前社交用户生成一个会员信息账号，以后这个社交账号对应指定的会员）
        //登录或者注册这个社交用户
        session.setAttribute(AuthServerConstant.LOGIN_USER, user);
        //重定向
        return "redirect:http://guli-mall.com";
    }
}
