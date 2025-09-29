package com.zcxd.base.vo;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName EmployeeTrainingRecordVO
 * @Description 员工培训记录信息
 * @author 秦江南
 * @Date 2021年5月18日下午4:51:25
 */
@ApiModel("员工培训记录")
@Data
public class EmployeeTrainingRecordVO {
	/**
	 * 员工培训记录Id
	 */
	@ApiModelProperty(value = "员工培训记录Id",required = true)
	@NotNull(message = "培训记录ID不能为空")
	@Min(value=1, message="培训记录ID不能小于1")
	private Long id;
	
	/**
	 * 员工id
	 */
	@ApiModelProperty(value = "员工ID",required = true)
	@NotNull(message = "员工ID不能为空")
	@Min(value=1, message="员工ID不能小于1")
	private Long empId;

	/**
	 * 培训主题id
	 */
	@ApiModelProperty(value = "培训主题ID",required = true)
	@NotNull(message = "培训主题ID不能为空")
	@Min(value=1, message="培训主题ID不能小于1")
	private Long trainId;

	/**
	 * 考核分数
	 */
	@ApiModelProperty(value = "考核分数",required = true)
	@NotNull(message = "考核分数不能为空")
	private Float score;

}
