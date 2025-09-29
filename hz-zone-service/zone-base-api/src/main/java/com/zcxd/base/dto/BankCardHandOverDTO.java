package com.zcxd.base.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 吞没卡处理
 */
@Data
public class BankCardHandOverDTO {

    /**
     * 上交方式类型
     */
    @ApiModelProperty(value = "交接方式（0 - 回笼交接，1 - 派送交接")
    private Integer type;

    /**
     * 吞没卡id列表
     */
    @ApiModelProperty(value = "吞没卡id列表")
    private List<Long> cardIdList;

}
