package com.zcxd.base.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AtmAdditionCashBatchVO extends AtmAdditionCashVO{

	/**
     * 线路
     */
    @ApiModelProperty(name="备用金所属线路",required = true)
    private String[] routeIds;
}
