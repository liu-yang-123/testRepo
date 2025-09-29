package com.zcxd.pda.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 清分钞盒统计DTO
 */
@Data
public class AtmClearBoxCountDTO {

    @ApiModelProperty(value = "银行id",required = true)
    private Long bankId;

    @ApiModelProperty(value = "银行名称",required = true)
    private String bankName;

    @ApiModelProperty(value = "统计盒数",required = true)
    private Integer total;
}
