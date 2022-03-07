package com.wch.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.wch.common.validator.annotation.ListValue;
import com.wch.common.validator.group.AddGroup;
import com.wch.common.validator.group.UpdateGroup;
import com.wch.common.validator.group.UpdateStatusGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author wch
 * @email 2879924329@qq.com
 * @date 2022-02-20 20:19:13
 *
 *
 * 使用JSR303校验
 * 1) 给java bean的字段添加合适的注解， 并且自定义一个message提示
 * 2）在发送请求处添加上一个@valid注解，
 * 3）给校验的bean后紧跟一个BindingResult,就可以获取到校验结果
 * 4）分组校验
 *     	@NotBlank(message = "品牌名不能为空", groups = {UpdateGroup.class, AddGroup.class})
 *     	给校验注解标注标注什么时候需要进行校验
 *     	controller层添加校验组 @Validated({AddGroup.class})
 *     	默认没有指定分组的校验注解， 在分组的情况下不生效， 只会在 @Validated情况下生效
 *
 *
 * 5）自定义校验
 *   编写自定义的校验注解
 *   编写自定义校验器
 *   关联自定义校验注解和校验器
 *
 *     @Documented
 * @Constraint(
 * //在这里可以指定多个校验器， 实现不同校验
 *         validatedBy = {ListValueConstraintValidator.class}
 * )
 * @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
 * @Retention(RetentionPolicy.RUNTIME)
 *
 *
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@NotNull(message = "修改必须指定id", groups = UpdateGroup.class)
	@Null(message = "新增不能指定id", groups = AddGroup.class)
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名不能为空", groups = {UpdateGroup.class, AddGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@URL(message = "logo必须是一个合法的url", groups = {UpdateGroup.class, AddGroup.class})
	@NotBlank(groups ={AddGroup.class} )
	private String logo;
	/**
	 * 介绍
	 */


	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@NotNull(groups = {AddGroup.class, UpdateStatusGroup.class})
	@ListValue(value = {0, 1}, groups = {AddGroup.class, UpdateStatusGroup.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotEmpty(groups ={AddGroup.class} )
	@Pattern(regexp = "^[a-zA-Z]$", message = "检索首字母必须是一个字母", groups = {UpdateGroup.class, AddGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(groups ={AddGroup.class} )
	@Min(value = 0, message = "排序必须大于等于0", groups = {UpdateGroup.class, AddGroup.class})
	private Integer sort;

}
