package com.zcxd.pda.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author shijin
 * @date 2021/4/19 14:17
 */
@ApiModel("金库盘点")
@Data
public class VaultCheckResultVO implements Serializable {

    @ApiModelProperty(value = "银行id",required = true)
    @NotNull(message = "缺少银行参数")
    private Long bankId;
    @ApiModelProperty(value = "操作人",required = true)
    @NotNull(message = "缺少盘点操作人")
    private Long whOpMan;
    @ApiModelProperty(value = "复核人",required = true)
    @NotNull(message = "缺少盘点复核人")
    private Long whCheckMan;
    @ApiModelProperty(value = "库管主管id")
    @NotNull(message = "缺少盘点主管")
    private Long whConfirmMan;
    @ApiModelProperty(value = "可用券金额",required = true)
    private BigDecimal usableAmount;
    @ApiModelProperty(value = "残损券金额",required = true)
    private BigDecimal badAmount;
    @ApiModelProperty(value = "五好券金额",required = true)
    private BigDecimal goodAmount;
    @ApiModelProperty(value = "未清分金额",required = true)
    private BigDecimal unclearAmount;
    @ApiModelProperty(value = "尾零金额",required = true)
    private BigDecimal remnantAmount;

}
