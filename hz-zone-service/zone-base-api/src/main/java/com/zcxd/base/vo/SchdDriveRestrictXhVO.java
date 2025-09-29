package com.zcxd.base.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 
 * @ClassName SchdDriveRestrictXhVO
 * @Description 特殊限行规则
 * @author shijin
 * @Date 2021年7月5日下午3:41:10
 */
@ApiModel("汽车特殊限行规则VO")
@Data
public class SchdDriveRestrictXhVO {

	@ApiModelProperty(value = "限行规则id")
    private Long id;

	@ApiModelProperty(value = "单双日",required = true)
	@NotNull(message = "单双日不能为空")
	private Integer dayType;

	@ApiModelProperty(value = "开始时间",required = true)
	@NotBlank(message = "开始时间不能为空")
	private String beginTime;

	@ApiModelProperty(value = "结束时间",required = true)
	@NotBlank(message = "结束时间不能为空")
	private String endTime;


	@ApiModelProperty(value = "允许尾号",required = true)
	@NotNull(message = "允许尾号不能为空")
	List<Integer> permitNumberList;

	@ApiModelProperty(value = "影响线路",required = true)
	@NotNull(message = "影响线路不能为空")
	List<Long> routeList;

	@ApiModelProperty(value = "部门Id",required = true)
	@NotNull(message = "部门Id不能为空")
	private Long departmentId;
}
