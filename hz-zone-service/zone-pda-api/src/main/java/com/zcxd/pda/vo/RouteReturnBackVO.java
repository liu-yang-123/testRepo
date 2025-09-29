package com.zcxd.pda.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author shijin
 * @date 2021/5/25 9:30
 */
@ApiModel("线路返回交接VO")
@Data
public class RouteReturnBackVO implements Serializable {

    @ApiModelProperty(value = "线路id",required = true)
    @NotNull(message = "缺少线路id")
    private Long routeId;

    @ApiModelProperty(value = "返回钞盒数量",required = true)
    @NotNull(message = "缺少钞盒数量")
    private Integer returnBoxCount;

    @ApiModelProperty(value = "返回钞袋数量",required = true)
    @NotNull(message = "缺少钞袋数量")
    private Integer returnBagCount;

    @ApiModelProperty(value = "交接经办人",required = true)
    @NotNull(message = "缺少交接经办人")
    private Long hoverOperMan;

    @ApiModelProperty(value = "交接复核人",required = true)
    @NotNull(message = "缺少交接复核人")
    private Long hoverCheckMan;
}
