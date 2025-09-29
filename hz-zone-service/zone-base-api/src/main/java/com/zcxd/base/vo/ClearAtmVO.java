package com.zcxd.base.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author songanwei
 * @date 2021-06-16
 */
@ApiModel("清分任务ATM对象")
@Data
public class ClearAtmVO implements Serializable {

    /**
     * 机构ID
     */
    @ApiModelProperty("银行机构ID")
    private Long bankId;

    /**
     * atm ID
     */
    @ApiModelProperty("ATM ID")
    private Long atmId;

    /**
     * 计划金额
     */
    @ApiModelProperty("清分计划金额")
    private BigDecimal planAmount;

}
