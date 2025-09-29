package com.zcxd.pda.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author shijin
 * @date 2021/5/21 9:30
 */
@ApiModel("线路配钞VO")
@Data
public class RouteDispCashVO implements Serializable {

    @ApiModelProperty(value = "线路id",required = true)
    @NotNull(message = "缺少线路id")
    private Long routeId;
    @ApiModelProperty(value = "配钞经办人",required = true)
    @NotNull(message = "缺少配钞库管经办人")
    private Long dispOperMan;
    @ApiModelProperty(value = "配钞复核人",required = true)
    @NotNull(message = "缺少配钞库管复核人")
    private Long dispCheckMan;

    @ApiModelProperty(value = "分配钞袋数量",required = true)
    @NotNull(message = "缺少钞袋数量")
    private Integer dispBagCount;
}
