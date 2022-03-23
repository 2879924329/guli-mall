package com.wch.gulimall.member.exception;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/22 20:52
 */
public class UserExistException extends RuntimeException{
    public UserExistException(){
        super("用户名已存在");
    }
}
