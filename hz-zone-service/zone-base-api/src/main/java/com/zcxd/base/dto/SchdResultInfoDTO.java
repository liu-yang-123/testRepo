package com.zcxd.base.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel("线路排班结果辅助类")
@Data
public class SchdResultInfoDTO {
    @ApiModelProperty(value = "唯一标识",required = true)
    private Long id;

    @ApiModelProperty(value = "计划日期",required = true)
    @NotNull(message = "计划日期不能为空")
    private Long planDay;

    /**
     * 线路组别
     */
    @ApiModelProperty(value = "线路",required = true)
    @NotNull(message = "线路不能为空")
    private String routeNo;

    /**
     * 车牌号码
     */
    @ApiModelProperty(value = "车牌号码",required = true)
    @NotNull(message = "车牌号码不能为空")
    private String vehicleNo;

    /**
     * 司机
     */
    @ApiModelProperty(value = "司机",required = true)
    @NotNull(message = "司机不能为空")
    private Long driver;

    /**
     * 护卫1
     */
    @ApiModelProperty(value = "护卫1",required = true)
    @NotNull(message = "护卫1不能为空")
    private Long scurityA;

    /**
     * 护卫2
     */
    @ApiModelProperty(value = "护卫2",required = true)
    @NotNull(message = "护卫2不能为空")
    private Long scurityB;

    /**
     * 钥匙员
     */
    @ApiModelProperty(value = "钥匙员",required = true)
    @NotNull(message = "钥匙员不能为空")
    private Long keyMan;

    /**
     * 密码操作员
     */
    @ApiModelProperty(value = "密码操作员",required = true)
    @NotNull(message = "密码操作员不能为空")
    private Long opMan;


    @ApiModelProperty(value = "车长",required = true)
    @NotNull(message = "车长不能为空")
    private Long leader;
}