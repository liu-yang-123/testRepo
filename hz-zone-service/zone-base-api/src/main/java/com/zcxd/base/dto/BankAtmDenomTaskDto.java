package com.zcxd.base.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author songanwei
 * @date 2021-06-28
 */
@Data
public class BankAtmDenomTaskDto {

    /**
     * 券别ID
     */
    private Long denomId;

    /**
     * 券别名称
     */
    private String denomName;

    /**
     * 任务金额
     */
    private BigDecimal taskAmount;

    /**
     * 确认状态
     */
    private Integer statusT;

}
