package com.zcxd.pda.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DenomRecordDTO {
    private Long id;
    private BigDecimal amount; //金额
    private Integer wadCount; //把数
    private Integer bundleCount; //捆数
    private Integer bagCount; //包数
    private Long piecesCount; //张数

    private Long denomId; //券别id
    private BigDecimal denomValue; //面值
    private String denomName;//券别名称
    private String curName;//货币类型
    private boolean isCoin; //是否硬币
}
