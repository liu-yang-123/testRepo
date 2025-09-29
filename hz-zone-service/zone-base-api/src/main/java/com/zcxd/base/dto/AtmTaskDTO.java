package com.zcxd.base.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author songanwei
 * @date 2021-08-12
 */
@Data
public class AtmTaskDTO {

    /**
     * ID
     */
    private Long id;

    /**
     * ATM ID
     */
    private Long atmId;

    /**
     * ATM终端编号
     */
    private String terNo;

    /**
     * 加钞金额
     */
    private BigDecimal amount;
}
