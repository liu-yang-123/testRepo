package com.zcxd.pda.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 统计线路中每种券别每个银行的加钞金额及任务数量DTO
 */
@Data
public class BankDenomSumDTO {
    private Long bankId; //银行
    private Long denomId; //加钞券别
    private BigDecimal totalAmount;   //金额
    private Float boxCount;   //加钞台数
    private Integer taskCount;   //任务数
}
