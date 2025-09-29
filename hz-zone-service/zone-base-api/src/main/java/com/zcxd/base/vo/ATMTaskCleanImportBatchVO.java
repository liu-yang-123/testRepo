package com.zcxd.base.vo;


import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName ATMTaskCleanImportBatchVO
 * @Description 导入批量清机任务
 * @author 秦江南
 * @Date 2021年8月4日下午3:38:43
 */
@ApiModel("导入批量清机任务")
@Data
public class ATMTaskCleanImportBatchVO {
	/**
	 * 线路id
	 */
	@ApiModelProperty(value = "线路id",required = true)
	@NotNull(message = "线路id不能为空")
	private Long routeId;
	
	/**
	 * 线路编号
	 */
	@ApiModelProperty(value = "线路编号",required = true)
	@NotBlank(message = "线路编号不能为空")
	private String routeNo;

	/**
	 * 任务日期
	 */
	@ApiModelProperty(value = "任务日期",required = true)
	@NotNull(message = "任务日期不能为空")
	private Long taskDate;
	
	/**
	 * 清机任务列表
	 */
	@ApiModelProperty(value = "清机任务列表",required = true)
	@NotNull(message = "清机任务列表不能为空")
	private List<ATMTaskCleanImportVO> atmTashCleanList;
}
