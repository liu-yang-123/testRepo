package com.zcxd.base.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author songanwei
 * @date 2021-06-15
 */
@ApiModel("ATM清分修改对象")
@Data
public class ClearTaskVO implements Serializable {

    /**
     * 主键
     */
    @ApiModelProperty("主键ID")
    private Long id;

    /**
     * ATM id
     */
    @ApiModelProperty("ATM ID")
    private Long atmId;

    /**
     * 计划清分金额
     */
    @ApiModelProperty("计划清分金额")
    private BigDecimal planAmount;

    /**
     * 实际清分金额
     */
    @ApiModelProperty("实际清分金额")
    private BigDecimal clearAmount;

    /**
     * 清分差错数据列表
     */
    @ApiModelProperty("清分差错数据")
    List<AtmClearErrorVO> errorList;
}
