package com.zcxd.pda.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * atm清分结果DTO
 */
@Data
public class AtmClearSumDTO {

    @ApiModelProperty(value = "线路id",required = true)
    private Long routeId;

    @ApiModelProperty(value = "线路编号",required = true)
    private String routeName;

    @ApiModelProperty(value = "所属银行ID",required = true)
    private Long bankId;

    @ApiModelProperty(value = "所属银行名称",required = true)
    private String bankName;

    @ApiModelProperty(value = "库存金额",required = true)
    private BigDecimal planAmount;

    @ApiModelProperty(value = "实际清分金额",required = true)
    private BigDecimal clearAmount;

    @ApiModelProperty(value = "差错金额",required = true)
    private BigDecimal diffAmount;

}
