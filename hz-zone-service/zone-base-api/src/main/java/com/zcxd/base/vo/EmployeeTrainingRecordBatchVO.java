package com.zcxd.base.vo;


import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName EmployeeTrainingRecordVO
 * @Description 批量员工培训记录信息
 * @author 秦江南
 * @Date 2021年5月18日下午4:51:25
 */
@ApiModel("批量员工培训记录")
@Data
public class EmployeeTrainingRecordBatchVO {
	/**
	 * 培训主题ID
	 */
	@ApiModelProperty(value = "培训主题ID",required = true)
	@NotNull(message = "培训主题ID不能为空")
	private Long trainId;

	/**
	 * 员工成绩列表
	 */
	@ApiModelProperty(value = "员工成绩列表",required = true)
	@NotNull(message = "员工成绩列表不能为空")
	private List<EmployeeTrainingRecordBatch> recordDtoList;

}
