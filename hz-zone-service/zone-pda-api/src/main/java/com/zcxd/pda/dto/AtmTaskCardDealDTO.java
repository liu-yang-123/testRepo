package com.zcxd.pda.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 吞没卡处理
 */
@Data
public class AtmTaskCardDealDTO {

    /**
     * 线路id
     */
    @ApiModelProperty(value = "线路id")
    private Long routeId;
    /**
     * 上交方式类型
     */
    @ApiModelProperty(value = "取卡方式（0 - 上交银行，1 - 客户自取")
    private Integer deliverType;
    /**
     * 处理时间
     */
    @ApiModelProperty(value = "处理时间")
    private Long dealTime;

    /**
     * 吞没卡id列表
     */
    @ApiModelProperty(value = "吞没卡id列表")
    private List<Long> cardIdList;

    /**
     * 接收人姓名
     */
    @ApiModelProperty(value = "接收人姓名")
    private String receiverName;
    /**
     * 接收人证件号
     */
    @ApiModelProperty(value = "接收人证件号")
    private String receiverIdno;
}
