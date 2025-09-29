package com.zcxd.base.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("清分任务券别信息")
@Data
public class TradeClearTaskRecordVO  implements Serializable {

    /**
     * 详情记录ID
     */
    private Long id;

    /**
     * 订单ID
     */
    @ApiModelProperty("订单ID")
    private Long taskerId;

    /**
     * 券别类型
     */
    @ApiModelProperty("券别类型")
    private Integer gbFlag;

    /**
     * 券别
     */
    @ApiModelProperty("券别ID")
    private Long denomId;

    /**
     * 金额
     */
    @ApiModelProperty("券别金额")
    private BigDecimal amount;

    /**
     * 张数
     */
    @ApiModelProperty("券别张数")
    private Integer count;
}
