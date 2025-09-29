package com.zcxd.db.model.result;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CountAmountResult<T> {

    private T key;

    /**
     * 查询数量
     */
    private Long count;

    /**
     * 查询金额
     */
    private BigDecimal amount;

}
