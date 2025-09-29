package com.zcxd.base.dto.report;

import lombok.Data;

/**
 * @author songanwei
 * @date 2021-10-12
 */
@Data
public class BankTaskReportDTO {

    /**
     * 银行ID
     */
    private Long bankId;

    /**
     * 机构名称
     */
    private String bankName;

    /**
     * 加钞台数
     */
    private Long cashNumber;

    /**
     * 清机台数
     */
    private Long cleanNumber;

    /**
     * 维护台数
     */
    private Long maintainNumber;

    /**
     * 撤销台数
     */
    private Long undoNumber;

    /**
     * 应急任务
     */
    private Long emergencyNumber;


}
