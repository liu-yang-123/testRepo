package com.zcxd.base.vo;

import com.zcxd.common.constant.DriverTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 
 * @ClassName SchdDriverAssignVO
 * @Description 司机主备班管理
 * @author shijin
 * @Date 2021年7月5日下午3:41:10
 */
@ApiModel("司机主备班管理")
@Data
public class SchdAlternateAssignVO {

	@ApiModelProperty(value = "记录id",required = false)
    private Long id;

	@ApiModelProperty(value = "员工Id",required = true)
	@NotNull(message = "员工不能为空")
    private Long employeeId;

    @ApiModelProperty(value = "负责线路")
    private String routeIds;

    @ApiModelProperty(value = "主班替班类型(0 - 主班，1 - 替班",required = true)
    @NotNull(message = "主班替班类型不能为空")
    private DriverTypeEnum alternateType;

    @ApiModelProperty(value = "主班车牌号码")
    private String vehicleNos;

    @ApiModelProperty(value = "部门Id",required = true)
    @NotNull(message = "部门Id不能为空")
    private Long departmentId;

    @ApiModelProperty(value = "计划Id",required = true)
    @NotNull(message = "计划Id不能为空")
    private Long planId;

    @ApiModelProperty(value = "类别(0 - 清机，1 - 护卫",required = true)
    @NotNull(message = "候选类别类型不能为空")
    private Integer planType;
}
