package com.zcxd.pda.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 钞盒清分信息DTO
 */
@Data
public class CashboxClearTimeDTO {
    @ApiModelProperty(value = "钞盒编号")
    private String boxNo; //钞盒编号
    @ApiModelProperty(value = "钞盒清分时间")
    private Long clearTime; //清分时间

}
