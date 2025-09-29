package com.zcxd.base.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * 
 * @ClassName SchdVacateSettingVO
 * @Description 休息计划设置管理
 * @author shijin
 * @Date 2021年7月5日下午3:41:10
 */
@ApiModel("休息计划设置")
@Data
public class SchdVacatePlanVO {

    /**
     * 自增id
     */
	@ApiModelProperty(value = "计划id",required = false)
    private Long id;

	@ApiModelProperty(value = "计划名称",required = true)
	@NotBlank(message = "计划名称不能为空")
    private String name;

	@ApiModelProperty(value = "开始日期",required = true)
	@NotNull(message = "开始日期不能为空")
    private Long beginDate;

    @ApiModelProperty(value = "开始日期",required = true)
    @NotNull(message = "开始日期不能为空")
    private Long endDate;

    @ApiModelProperty(value = "部门id",required = true)
    @NotNull(message = "部门id不能为空")
    private Long departmentId;

    @ApiModelProperty(value = "计划类别(0 - 业务员，司机，1 - 护卫)",required = true)
    @NotNull(message = "计划类别不能为空")
    private Integer planType;
}
