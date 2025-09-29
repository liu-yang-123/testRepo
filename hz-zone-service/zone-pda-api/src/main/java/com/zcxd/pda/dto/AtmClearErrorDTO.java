package com.zcxd.pda.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 差错明细记录
 */
@Data
public class AtmClearErrorDTO {

    @ApiModelProperty(value = "券别id")
    private Long denomId;

    @ApiModelProperty(value = "数量(假钞时填 1)")
    private Integer count;

    @ApiModelProperty(value = "冠字号(有假钞时登记")
    private String rmbSn;
}
