package com.zcxd.base.vo;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ATMTaskMoveVO extends BatchIdsVO{
	/**
     * 目标线路Id
     */
	@ApiModelProperty(value = "目标线路Id",required = true)
	@NotNull(message = "routeId不能为空")
    private Long routeId;
}
