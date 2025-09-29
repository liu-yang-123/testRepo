package com.zcxd.base.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName EmployeeTrainingSubject
 * @Description 员工培训主题
 * @author 秦江南
 * @Date 2021年5月18日下午3:56:57
 */
@ApiModel("员工培训主题")
@Data
public class EmployeeTrainingSubjectVO{

    /**
     * 自增主键
     */
	@ApiModelProperty(value = "员工培训主题Id",required = false)
    private Long id;

    /**
     * 培训日期
     */
	@ApiModelProperty(value = "培训日期",required = true)
	@NotNull(message = "培训日期不能为空")
    private Long trainDate;

    /**
     * 培训类别
     */
	@ApiModelProperty(value = "培训类别",required = true)
    @NotBlank(message = "培训类别不能为空")  
    @Size(max=32,message="培训类别最大长度为32")
    private String trainType;

    /**
     * 培训主题
     */
	@ApiModelProperty(value = "培训主题",required = true)
    @NotBlank(message = "培训主题不能为空")  
    @Size(max=32,message="培训主题最大长度为32")
    private String trainTitle;

    /**
     * 培训内容
     */
	@ApiModelProperty(value = "培训内容",required = true)
    @NotBlank(message = "培训内容不能为空")  
    @Size(max=32,message="培训内容最大长度为32")
    private String trainContent;

    /**
     * 培训老师
     */
	@ApiModelProperty(value = "培训老师",required = true)
    @NotBlank(message = "培训老师不能为空")  
    @Size(max=32,message="培训老师最大长度为32")
    private String trainer;

    /**
     * 培训地点
     */
	@ApiModelProperty(value = "培训地点",required = false)
    @Size(max=32,message="培训地点最大长度为32")
    private String place;

    /**
     * 培训时长
     */
	@ApiModelProperty(value = "培训时长",required = false)
    private Integer times;

    /**
     * 考核方式
     */
	@ApiModelProperty(value = "考核方式",required = false)
    @Size(max=32,message="考核方式最大长度为32")
    private String test;

}
