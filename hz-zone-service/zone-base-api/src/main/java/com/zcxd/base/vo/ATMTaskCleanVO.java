package com.zcxd.base.vo;


import java.math.BigDecimal;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName ATMTaskCleanVO
 * @Description 清机任务
 * @author 秦江南
 * @Date 2021年6月22日下午3:47:47
 */
@ApiModel("清机任务")
@Data
public class ATMTaskCleanVO {
	/**
	 * ATM设备id
	 */
	@ApiModelProperty(value = "ATM设备id",required = true)
	@NotNull(message = "ATM设备id不能为空")
	private Long atmId;
	
	/**
	 * 加钞金额
	 */
	@ApiModelProperty(value = "加钞金额",required = true)
	@NotNull(message = "加钞金额不能为空")
	private BigDecimal amount;

//	/**
//	 * 备用金排班标志
//	 */
//	@ApiModelProperty(value = "备用金排班标志",required = false)
//	private Integer backupFlag;
	
	/**
	 * 任务备注
	 */
	@ApiModelProperty(value = "任务备注",required = false)
	@Size(max=64,message="任务备注最大长度为64")
	private String comments;
	
}
