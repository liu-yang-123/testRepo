package com.zcxd.base.dto.report;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author songanwei
 * @date 2021-10-20
 */
@Data
public class BankReceivePaymentReportDTO implements Serializable {

    /**
     * 机构ID
     */
    private Long bankId;

    /**
     * 银行机构名称
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
     * 领款金额
     */
    private BigDecimal receiveAmount;

    /**
     * 缴款金额
     */
    private BigDecimal paymentAmount;
}
