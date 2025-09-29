package com.zcxd.base.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * 收费规则
 * </p>
 *
 * @author admin
 * @since 2022-05-22
 */
@Data
public class TradeClearChargeRuleDTO{


    /**
     * 主键
     */
    private Long id;

    /**
     * 所属事业部
     */
    private Long departmentId;

    /**
     * 机构id
     */
    private Long bankId;

    /**
     * 券别类型：1 - 完整券，2 - 残损券
     */
    private Integer gbFlag;

    /**
     * 票面面额
     */
    private String denom;

    /**
     * 票面名称
     */
    private String denomName;

    /**
     * 纸硬币(P - 纸币，C - 硬币）
     */
    private String attr;

    /**
     * 单价
     */
    private BigDecimal price;
}
