package com.zcxd.pda.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author shijin
 * @date 2021/5/21 9:30
 */
@ApiModel("线路分配钞袋VO")
@Data
public class RouteDispBagVO implements Serializable {

    @ApiModelProperty(value = "线路id",required = true)
    @NotNull(message = "缺少线路id")
    private Long routeId;
    @ApiModelProperty(value = "分配钞袋数量",required = true)
    @NotNull(message = "缺少钞袋数量")
    private Integer dispBagCount;
}
