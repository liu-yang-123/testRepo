package com.zcxd.base.vo;


import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName ATMTaskClearImportBatchVO
 * @Description 导入批量清分任务
 * @author 秦江南
 * @Date 2021年8月4日下午3:38:43
 */
@ApiModel("导入批量清分任务")
@Data
public class ATMTaskClearImportBatchVO {
	
	/**
	 * 线路编号
	 */
	@ApiModelProperty(value = "线路编号",required = true)
	@NotBlank(message = "线路编号不能为空")
	private String routeNo;

	/**
	 * 清机任务列表
	 */
	@ApiModelProperty(value = "清机任务列表",required = true)
	@NotNull(message = "清机任务列表不能为空")
	private List<ATMTaskClearImportVO> atmTashClearList;
}
