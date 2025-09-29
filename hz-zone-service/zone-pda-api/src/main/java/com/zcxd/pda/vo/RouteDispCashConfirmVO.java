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
@ApiModel("线路配钞复核VO")
@Data
public class RouteDispCashConfirmVO implements Serializable {

    @ApiModelProperty(value = "线路id",required = true)
    @NotNull(message = "缺少线路id")
    private Long routeId;
    @ApiModelProperty(value = "钞盒列表",required = true)
    private String[] boxRfids;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "用户密码")
    private String password;
}
