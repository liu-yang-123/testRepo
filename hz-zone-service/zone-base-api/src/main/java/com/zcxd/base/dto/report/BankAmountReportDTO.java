package com.zcxd.base.dto.report;

import lombok.Data;
import java.math.BigDecimal;

/**
 * @author songanwei
 * @date 2021-10-13
 */
@Data
public class BankAmountReportDTO {

    /**
     * 机构ID
     */
    private Long bankId;

    /**
     * 机构名称
     */
    private String bankName;

    /**
     * 券别ID
     */
    private Long denomId;

    /**
     * 券别名称
     */
    private String denomName;

    /**
     * 加款台数
     */
    private Long cashNumber;

    /**
     * 加款金额
     */
    private BigDecimal cashAmount;

    /**
     * 撤销台数
     */
    private Long undoNumber;

    /**
     * 撤销金额
     */
    private BigDecimal undoAmount;

}
