package com.wch.gulimall.authserver.controller;

import com.wch.common.utils.R;
import com.wch.gulimall.authserver.config.GiteeLoginConfig;
import com.wch.gulimall.authserver.service.GiteeOauth2Service;
import com.wch.gulimall.authserver.vo.GiteeUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/23 22:24
 *
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
     * @return
     */
    @GetMapping("/oauth2/success")
    public String giteeLogin(@RequestParam(name = "code") String code,
                        @RequestParam(name = "state") String state){
        //使用GetUser方法将携带的accessToken链接发送至Gitee服务器获取用户的信息
        giteeLoginConfig.setState(state);
        giteeLoginConfig.setCode(code);
        String accessToken = giteeOauth2Service.getAccessToken(giteeLoginConfig);
        GiteeUser user = giteeOauth2Service.getUser(accessToken);
        System.out.println(user.getName());
        System.out.println(user.getId());
        System.out.println(user.getBio());
        //重定向
        return "redirect:http://guli-mall.com";
    }
}
