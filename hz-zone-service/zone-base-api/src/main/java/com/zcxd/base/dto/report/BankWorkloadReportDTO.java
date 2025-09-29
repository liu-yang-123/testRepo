package com.zcxd.base.dto.report;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author songanwei
 * @date 2021-10-14
 */
@Data
public class BankWorkloadReportDTO implements Serializable {

    /**
     * 序号
     */
    private Integer index;

    /**
     * 姓名
     */
    private String name;

    /**
     * 加款台数
     */
    private Long cashNumber;

    /**
     * 加款金额
     */
    private BigDecimal amount;

    /**
     * 清机台数
     */
    private Long cleanNumber;
    /**
     * 维护台数
     */
    private Long maintainNumber;

    /**
     * 维护台数
     */
    private Long totalNumber;
    /**
     * 上班天数
     */
    private Long workDay;
}
