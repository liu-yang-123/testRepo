package com.zcxd.pda.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 清分钞盒绑定DTO
 */
@Data
public class CashboxClearDTO {

    /**
     * 清分设备编号
     */
    @ApiModelProperty(value = "清分机编号")
    private String deviceNo;
    /**
     * 清分人
     */
    @ApiModelProperty(value = "清分人")
    private Long clearMan;
    /**
     * 复核人
     */
    @ApiModelProperty(value = "复核人")
    private Long checkMan;

    /**
     * 所属线路
     */
    @ApiModelProperty(value = "所属银行")
    private Long bankId;
    /**
     * 绑定钞盒
     */
    @ApiModelProperty(value = "钞盒对应券别类型（100，或 10）")
    private Integer denomValue; //券别面额： 100，或 10

    @ApiModelProperty(value = "钞盒列表")
    private List<CashboxClearTimeDTO> cashboxClearTimeDTOS;
}
