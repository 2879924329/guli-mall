package com.wch.gulimall.authserver.service;

import com.wch.gulimall.authserver.config.GiteeLoginConfig;
import com.wch.gulimall.authserver.vo.GiteeUser;


/**
 * @author wch
 * @version 1.0
 * @date 2022/3/23 22:18
 */
public interface GiteeOauth2Service {
    String getAccessToken(GiteeLoginConfig giteeLoginConfig);
    GiteeUser getUser(String accessToken);
}
