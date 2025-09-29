package com.zcxd.pda.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 金库出入库单DTO
 */
@Data
public class AtmClearBankDTO {
    @ApiModelProperty(value = "银行id")
    private Long id;
    @ApiModelProperty(value = "银行名称")
    private String bankName;
}
