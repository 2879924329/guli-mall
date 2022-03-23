package com.wch.gulimall.member.exception;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/22 20:52
 */
public class PhoneExistException extends RuntimeException{
    public PhoneExistException(){
        super("手机号已存在");
    }
}
