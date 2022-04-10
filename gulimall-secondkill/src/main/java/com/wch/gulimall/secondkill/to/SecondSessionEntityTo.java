package com.wch.gulimall.secondkill.to;


import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/10 22:04
 */
@Data
public class SecondSessionEntityTo {
    /**
     * id
     */
    private Long id;
    /**
     * 场次名称
     */
    private String name;
    /**
     * 每日开始时间
     */
    private Date startTime;
    /**
     * 每日结束时间
     */
    private Date endTime;
    /**
     * 启用状态
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;

    private List<SecondKillRelationEntityTo> relationEntities;
}
