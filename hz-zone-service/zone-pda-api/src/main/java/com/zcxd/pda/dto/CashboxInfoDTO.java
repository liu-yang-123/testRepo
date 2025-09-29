package com.zcxd.pda.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 钞盒信息
 */
@Data
public class CashboxInfoDTO {

    @ApiModelProperty(value = "清分人")
    private String clearManName;
    @ApiModelProperty(value = "复核人")
    private String checkManName;
    @ApiModelProperty(value = "清点时间")
    private Long clearTime;
    @ApiModelProperty(value = "券别")
    private String denomName;
    @ApiModelProperty(value = "已分配线路")
    private String routeName;
    @ApiModelProperty(value = "当前状态")
    private Integer statusT;
    @ApiModelProperty(value = "当前状态描述")
    private String statusText;
}
