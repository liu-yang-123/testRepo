package com.zcxd.pda.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 银行库存统计DTO
 */
@Data
public class VaultVolumDTO {
    private Long bankId; //银行ID
    private String bankName; //银行名称
    private BigDecimal amount;   //总余额
}
