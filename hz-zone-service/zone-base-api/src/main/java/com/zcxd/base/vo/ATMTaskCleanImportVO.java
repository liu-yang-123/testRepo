package com.zcxd.base.vo;


import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName ATMTaskCleanImportVO
 * @Description 导入清机任务
 * @author 秦江南
 * @Date 2021年8月5日下午4:51:51
 */
@ApiModel("导入清机任务")
@Data
public class ATMTaskCleanImportVO {
	/**
	 * ATM设备id
	 */
	@ApiModelProperty(value = "ATM设备id",required = true)
	@NotNull(message = "ATM设备id不能为空")
	private Long atmId;
	
	/**
	 * ATM编号
	 */
	@ApiModelProperty(value = "ATM编号",required = true)
	@NotBlank(message = "ATM编号不能为空")
	private String terNo;
	
	/**
	 * 加钞金额
	 */
	@ApiModelProperty(value = "加钞金额",required = true)
	@NotNull(message = "加钞金额不能为空")
	private BigDecimal amount;

	/**
	 * 备用金排班标志
	 */
	@ApiModelProperty(value = "备用金排班标志",required = false)
	private Integer backupFlag;
	
	/**
	 * 任务备注
	 */
	@ApiModelProperty(value = "任务备注",required = false)
	@Size(max=64,message="任务备注最大长度为64")
	private String comments;
	
}
