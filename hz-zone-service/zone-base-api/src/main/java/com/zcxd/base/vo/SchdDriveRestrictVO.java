package com.zcxd.base.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 
 * @ClassName SchdDriveRestrictVO
 * @Description 普通限行规则
 * @author shijin
 * @Date 2021年7月5日下午3:41:10
 */
@ApiModel("汽车普通限行规则VO")
@Data
public class SchdDriveRestrictVO {

	@ApiModelProperty(value = "限行规则id")
    private Long id;

	@ApiModelProperty(value = "限行日(星期几，存序号)",required = true)
	@NotNull(message = "限行日不能为空")
	private Integer weekday;

	@ApiModelProperty(value = "早高峰开始时间",required = true)
	@NotBlank(message = "早高峰开始时间不能为空")
	private String beginTimeAm;

	@ApiModelProperty(value = "早高峰结束时间",required = true)
	@NotBlank(message = "早高峰结束时间不能为空")
	private String endTimeAm;

	@ApiModelProperty(value = "晚高峰开始时间",required = true)
	@NotBlank(message = "晚高峰开始时间不能为空")
	private String beginTimePm;

	@ApiModelProperty(value = "晚高峰结束时间",required = true)
	@NotBlank(message = "晚高峰结束时间不能为空")
	private String endTimePm;

	@ApiModelProperty(value = "限行尾号",required = true)
	@NotNull(message = "限行尾号不能为空")
	List<Integer> forbidNumberList;

	@ApiModelProperty(value = "部门Id",required = true)
	@NotNull(message = "部门Id不能为空")
	private Long departmentId;
}
