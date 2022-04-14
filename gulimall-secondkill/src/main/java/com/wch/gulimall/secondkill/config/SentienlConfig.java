package com.wch.gulimall.secondkill.config;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.wch.common.exception.Code;
import com.wch.common.utils.R;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/14 20:18
 */
@Configuration
public class SentienlConfig implements BlockExceptionHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws Exception {
        R error = R.error(Code.TO_MANY_REQUEST.getCode(), Code.TO_MANY_REQUEST.getMessage());
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json");
        httpServletResponse.getWriter().write(JSON.toJSONString(error));
    }
}
