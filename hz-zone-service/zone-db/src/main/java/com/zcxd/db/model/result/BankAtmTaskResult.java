package com.zcxd.db.model.result;

import lombok.Data;
import java.math.BigDecimal;

/**
 * @author songanwei
 * @date 2021-08-11
 */
@Data
public class BankAtmTaskResult {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * ATM ID
     */
    private Long atmId;

    /**
     * 线路ID
     */
    private Long routeId;

    /**
     * 配钞金额
     */
    private BigDecimal amount;

    /**
     * 券别ID
     */
    private Long denomId;


    /**
     * 设备编号
     */
    private String terNo;
}
