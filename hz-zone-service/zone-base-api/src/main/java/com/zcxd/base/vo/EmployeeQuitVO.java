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
 * @ClassName EmployeeQuitDto
 * @Description 离职处理
 * @author 秦江南
 * @Date 2021年5月17日下午2:44:30
 */
@ApiModel("离职处理")
@Data
public class EmployeeQuitVO {
	/**
	 * 员工档案Id
	 */
	@ApiModelProperty(value = "员工档案Id",required = true)
	@NotNull(message = "员工档案Id不能为空")
	private Long id;

	/**
	 * 离职日期
	 */
	@ApiModelProperty(value = "离职日期",required = true)
	@NotNull(message = "离职日期不能为空")
	private Long quitDate;
	
	/**
	 * 离职原因
	 */
    @ApiModelProperty(value = "离职原因",required = true)
    @NotBlank(message = "离职原因不能为空")
    @Size(max=64,message="reasons最大长度为64")
	private String reasons;
}
