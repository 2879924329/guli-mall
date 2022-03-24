package com.wch.gulimall.authserver.service.impl;

import com.alibaba.fastjson.JSON;
import com.wch.gulimall.authserver.config.GiteeLoginConfig;
import com.wch.gulimall.authserver.service.GiteeOauth2Service;
import com.wch.gulimall.authserver.vo.GiteeUser;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/23 22:20
 */
@Service
public class GiteeOauth2ServiceImpl implements GiteeOauth2Service {
    @Override
    public String getAccessToken(GiteeLoginConfig giteeLoginConfig){
    /** 通过okhttp向gitee认证服务器发送code以获取返回的AccessToken
     *传入的待获取认证DTO，包含应用id、密钥、重定向地址等
     * @return AccessToken
     */
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(giteeLoginConfig));
        Request request = new Request.Builder()
                .url("https://gitee.com/oauth/token?grant_type=authorization_code&" +
                        "code=" + giteeLoginConfig.getCode() + "&" +
                        "client_id=" + giteeLoginConfig.getClientId() + "&" +
                        "redirect_uri=" + giteeLoginConfig.getRedirectUrl() + "&" +
                        "client_secret=" + giteeLoginConfig.getClientSecret())
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            String string = response.body().string();
            return string.split("\"")[3];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 应用通过 access_token 访问 Open API 使用用户数据。
     * @param accessToken 传入的accessToken用于向Gitee服务器请求用户数据
     * @return
     */
    @Override
    public GiteeUser getUser(String accessToken){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://gitee.com/api/v5/user?access_token=" + accessToken)
                .build();
        try {
            Response response = client.newCall(request).execute();
            assert response.body() != null;
            String string = response.body().string();
            //将String的json对象自动的解析成GiteeUser类的对象
            return JSON.parseObject(string, GiteeUser.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }

}
