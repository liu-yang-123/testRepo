package com.zcxd.base.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 
 * @ClassName SchdDriverAssignVO
 * @Description 司机主备班管理
 * @author shijin
 * @Date 2021年7月5日下午3:41:10
 */
@ApiModel("司机主备班管理")
@Data
public class SchdDriverAssignVO {

	@ApiModelProperty(value = "记录id",required = false)
    private Long id;

	@ApiModelProperty(value = "司机Id",required = true)
	@NotNull(message = "司机不能为空")
    private Long driver;

    @ApiModelProperty(value = "负责线路")
    private Long routeId;

    @ApiModelProperty(value = "主班替班类型(0 - 主班，1 - 替班",required = true)
    @NotNull(message = "主班替班类型不能为空")
    private Integer driverType;

    @ApiModelProperty(value = "排班方式(0 - 随机，1 - 固定",required = true)
    @NotNull(message = "排班方式不能为空")
    private Integer assignType;

    @ApiModelProperty(value = "主班车牌号码")
    private String vehicleNo;

    @ApiModelProperty(value = "部门Id",required = true)
    @NotNull(message = "部门Id不能为空")
    private Long departmentId;
}
