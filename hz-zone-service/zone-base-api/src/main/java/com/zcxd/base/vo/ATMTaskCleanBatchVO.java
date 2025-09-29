package com.zcxd.base.vo;


import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName ATMTaskBatchVO
 * @Description 批量清机任务
 * @author 秦江南
 * @Date 2021年6月2日下午6:46:05
 */
@ApiModel("批量清机任务")
@Data
public class ATMTaskCleanBatchVO {
//	/**
//	 * 线路id
//	 */
//	@ApiModelProperty(value = "线路id",required = true)
//	@NotNull(message = "线路id不能为空")
//	private Long routeId;

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
	private List<ATMTaskCleanVO> atmTashCleanList;
}
