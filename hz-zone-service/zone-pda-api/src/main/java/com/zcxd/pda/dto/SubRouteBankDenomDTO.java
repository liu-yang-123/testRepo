package com.zcxd.pda.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 线路加钞券别统计DTO
 */
@Data
public class SubRouteBankDenomDTO {
    private Long bankId;
    private String bankName;   //银行名称
    private BigDecimal totalAmount; //总金额
    private Long denomId;
    private String denomName; //券别名称
    private Float bundleCount; //捆数/盒数
    private Integer totalTask; //清机台数
}
