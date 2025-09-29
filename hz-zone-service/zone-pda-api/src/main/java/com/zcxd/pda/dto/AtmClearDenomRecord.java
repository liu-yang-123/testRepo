package com.zcxd.pda.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * atm清分券别明细
 */
@Data
public class AtmClearDenomRecord {
    /**
     * 券别id
     */
    private Long id;
    /**
     * 库存金额
     */
    private String name;
}
