package com.zcxd.pda.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 金库出入库单DTO
 */
@Data
public class VaultOrderDTO {
    private Long id;
    private Long orderDate;
    private Integer orderType;
    private Integer statusT;
    private Long bankId;
    private  String bankName;
    private  String comments;
    private BigDecimal OrderAmount;
}
