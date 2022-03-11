package com.wch.gulimall.product.exception;

import com.wch.common.exception.Code;
import com.wch.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/1 21:50
 *
 * 统一异常处理
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.wch.gulimall.product.controller")
public class GulimallException {
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handValidException(MethodArgumentNotValidException e){
     log.error("数据校验出现问题: {}， 异常类型: {}", e.getMessage(), e.getClass());
        BindingResult bindingResult = e.getBindingResult();
        HashMap<String, String> map = new HashMap<>();
        bindingResult.getFieldErrors().forEach(fieldError -> map.put(fieldError.getField(), fieldError.getDefaultMessage()));
        return R.error(Code.VALID_EXCEPTION.getCode(), Code.VALID_EXCEPTION.getMessage()).put("data", map);
    }

    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable throwable){
        log.error("错误: ", throwable);
        return R.error(Code.UNKNOWN_EXCEPTION.getCode(), Code.UNKNOWN_EXCEPTION.getMessage());
    }
}
