package com.zcxd.db.model.result;

import lombok.Data;
import java.math.BigDecimal;

/**
 * @author songanwei
 * @date 2021-10-22
 */
@Data
public class BankAmountResult {

    /**
     * 银行机构ID
     */
    private Long bankId;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 日期
     */
    private String date;

}
