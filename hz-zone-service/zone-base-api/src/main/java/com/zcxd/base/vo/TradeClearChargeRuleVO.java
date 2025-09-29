package com.zcxd.base.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 清分收费规则VO
 */
@Data
public class TradeClearChargeRuleVO {

    private Long id;
    /**
     * 部门id
     */
    @NotNull(message = "部门不能为空")
    private Long departmentId;
    /**
     * 机构id
     */
    @NotNull(message = "机构不能为空")
    private Long bankId;
    /**
     * 券别类型(残损券 - 1，完整券 - 2)
     */
    @NotNull(message = "券别类型不能为空")
    private int gbFlag;
    /**
     * 纸硬币（P - 纸币，C - 硬币）
     */
    private String attr;
    /**
     * 券别
     */
    private String denom;
    /**
     * 价格
     */
    private BigDecimal price;
}
