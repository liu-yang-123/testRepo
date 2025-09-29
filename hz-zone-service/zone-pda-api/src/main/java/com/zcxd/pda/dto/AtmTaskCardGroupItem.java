package com.zcxd.pda.dto;

import io.swagger.models.auth.In;
import lombok.Data;

/**
 * 网点分组吞没卡列表DTO
 */
@Data
public class AtmTaskCardGroupItem {
    private Long id;
    /**
     * 吞没卡号
     */
    private String cardNo;

    /**
     * 开卡行
     */
    private String cardBank;

    /**
     * 吞没卡状态
     */
    private Integer statusT;

    /**
     * 类别(0 - 银行卡，1 - 回执单)
     */
    private Integer category;
}
