package com.zcxd.base.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("订单券别视图对象")
@Data
public class VaultOrderRecordVO  implements Serializable {

    /**
     * 详情记录ID
     */
    private Long id;

    /**
     * 订单ID
     */
    @ApiModelProperty("订单ID")
    private Long orderId;

    /**
     * 券别类型
     */
    @ApiModelProperty("券别类型")
    private Integer denomType;

    /**
     * 券别
     */
    @ApiModelProperty("券别ID")
    private Long denomId;

    /**
     * 券别文本
     */
    @ApiModelProperty("描述")
    private String denomText;

    /**
     * 金额
     */
//    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty("券别金额")
    private BigDecimal amount;

    /**
     * 张数
     */
    @ApiModelProperty("券别张数")
    private Integer count;
}
