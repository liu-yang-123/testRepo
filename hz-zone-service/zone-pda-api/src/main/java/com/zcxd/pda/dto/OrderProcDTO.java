package com.zcxd.pda.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 出入库订单操作DTO
 */
@ApiModel("出入库订单DTO")
@Data
public class OrderProcDTO implements Serializable {
    @ApiModelProperty(value = "订单id",required = true)
    @NotNull(message = "缺少订单id")
    private Long orderId; //订单id

    @ApiModelProperty(value = "经办人",required = true)
    @NotNull(message = "缺少经办人")
    private Long whOpMan; //经办人

    @ApiModelProperty(value = "复核人",required = true)
    @NotNull(message = "缺少复核人")
    private Long whCheckMan;   //复核人

    @ApiModelProperty(value = "审核主管",required = true)
    @NotNull(message = "缺少审核主管")
    private Long whConfirmMan; //主管
}
