package com.zcxd.pda.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * atm清分结果DTO
 */
@Data
public class AtmClearTotalDTO {

    @ApiModelProperty(value = "线路清分统计",required = true)
    private List<AtmClearSumDTO> clearAmountSums;
    @ApiModelProperty(value = "钞盒绑定统计",required = true)
    private List<AtmClearBoxCountDTO> clearBoxCounts;
}
