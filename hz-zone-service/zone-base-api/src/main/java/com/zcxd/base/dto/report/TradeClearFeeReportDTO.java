package com.zcxd.base.dto.report;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * add by shijin
 * date 2022-05-26
 */

@Data
public class TradeClearFeeReportDTO implements Serializable {

    private Long bankId;
    /**
     * 银行机构名称
     */
    private String bankName;
    /**
     * 券别类型（1- 残损券，2 - 完整券）
     */
    private Integer denomType;
//    /**
//     * 券别类型名称
//     */
//    private String denomTypeText;
    /**
     * 纸硬币标志
     */
    private String denomAttr;
    /**
     * 券别名称
     */
    private String denomName;
    /**
     * 券别value
     */
    private String denomValues;
    /**
     * 清分总金额
     */
    private BigDecimal totalAmount;
    /**
     * 捆数
     */
    private Integer bundles;
    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 费用
     */
    private BigDecimal feeAmount;
}
