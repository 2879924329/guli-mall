package com.wch.common.validator;

import com.wch.common.validator.annotation.ListValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/1 22:40
 *
 * 自定义校验器
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue, Integer> {


    private Set<Integer> set = new HashSet<>();
    /**
     * 初始化方法
     * @param constraintAnnotation
     */
    @Override
    public void initialize(ListValue constraintAnnotation) {
        int[] value = constraintAnnotation.value();
        for (int valueIndex: value){
            set.add(valueIndex);
        }
    }

    /**
     * 判断是否校验成功
     * @param integer  需要校验的值
     * @param constraintValidatorContext
     * @return
     */
    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        return set.contains(integer);
    }
}
