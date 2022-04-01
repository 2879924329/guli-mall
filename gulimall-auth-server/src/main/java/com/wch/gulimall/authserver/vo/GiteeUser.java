package com.wch.gulimall.authserver.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/31 20:39
 */
@Data
public class GiteeUser implements Serializable {
    private Long id;
    private String bio;
    private String name;
}
