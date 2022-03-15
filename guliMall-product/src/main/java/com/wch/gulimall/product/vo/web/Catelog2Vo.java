package com.wch.gulimall.product.vo.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Period;
import java.util.List;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/11 11:39
 *
 * 二级分类vo
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Catelog2Vo {
    private String catalog1Id;
    private List<Catelog3Vo> catalog3List;
    private String id;
    private String name;

    /**
     * 三级分类vo
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Catelog3Vo {
        private String catalog2Id;
        private String id;
        private String name;
    }
}
