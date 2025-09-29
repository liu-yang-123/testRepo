package com.zcxd.pda.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * atm清分券别DTO
 */
@Data
public class AtmClearDenomDTO {

    /**
     * 无版本券别
     */
    List<AtmClearDenomRecord> denoms;

    /**
     * 残缺币券别
     */
    List<AtmClearDenomRecord> badDenoms;
}
