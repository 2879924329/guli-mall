package com.wch.common.to;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/23 22:22
 */
@Data
public class GiteeUser implements Serializable {
        private String name;
        private Long id;
        private String bio;
}
