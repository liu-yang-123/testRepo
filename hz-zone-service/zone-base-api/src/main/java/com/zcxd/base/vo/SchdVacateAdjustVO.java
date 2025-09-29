package com.zcxd.base.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 
 * @ClassName SchdVacateAdjustVO
 * @Description 休息计划临时调整管理
 * @author shijin
 * @Date 2021年7月5日下午3:41:10
 */
@ApiModel("休息计划调整")
@Data
public class SchdVacateAdjustVO {

    /**
     * 唯一id
     */
	@ApiModelProperty(value = "调整计划id")
    private Long id;

	@ApiModelProperty(value = "调整日期(yyyy-MM-dd)",required = true)
	@NotBlank(message = "调整日期不能为空")
    private String adjustDate;

	@ApiModelProperty(value = "调整类型",required = true)
	@NotNull(message = "调整类型不能为空")
    private Integer adjustType;

    @ApiModelProperty(value = "员工id",required = true)
    @NotNull(message = "员工id不能为空")
    private Long empId;

    @ApiModelProperty(value = "替班员工id",required = true)
    private Long repEmpId;

    @ApiModelProperty(value = "部门id",required = true)
    @NotNull(message = "部门id不能为空")
    private Long departmentId;

    @ApiModelProperty(value = "计划类别(0 - 业务员，司机，1 - 护卫)",required = true)
    @NotNull(message = "计划类别不能为空")
    private Integer planType;

    @ApiModelProperty(value = "原因")
    private String reason;
}
