package com.zcxd.base.vo;

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
public class EmployeeTrainingRecordBatch {
	
	/**
	 * 员工id
	 */
	@ApiModelProperty(value = "员工ID",required = true)
	@NotNull(message = "员工ID不能为空")
	private Long empId;

	/**
	 * 考核分数
	 */
	@ApiModelProperty(value = "考核分数",required = true)
	@NotNull(message = "考核分数不能为空")
	private Float score;

}
