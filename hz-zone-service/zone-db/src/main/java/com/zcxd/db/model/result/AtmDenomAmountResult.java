package com.zcxd.db.model.result;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author songanwei
 * @date 2021-06-30
 */
@Data
public class AtmDenomAmountResult {

    /**
     * 券别ID
     */
    private Long denomId;

    /**
     * 券别金额
     */
    private BigDecimal taskAmount;

}
