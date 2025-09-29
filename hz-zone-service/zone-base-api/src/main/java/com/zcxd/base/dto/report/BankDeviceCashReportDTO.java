package com.zcxd.base.dto.report;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author songanwei
 * @date 2021-10
 */
@Data
public class BankDeviceCashReportDTO implements Serializable {

    /**
     * 序号
     */
    private Integer index;

    /**
     * 设备ID
     */
    private Long deviceId;

    /**
     * 设备号
     */
    private String terNo;

    /**
     * 所属线路
     */
    private String routeNo;

    /**
     * 总任务
     */
    private Long totalTask;

    /**
     * 加钞任务数量
     */
    private Long cashNumber;
    /**
     * 清机任务数量
     */
    private Long cleanNumber;

    /**
     * 维护任务数
     */
    private Long maintainNumber;

    /**
     * 加钞金额
     */
    private BigDecimal cashAmount;

    /**
     * 回库金额
     */
    private BigDecimal backAmount;

    /**
     * 加钞批次（天/次）
     */
    private Double cashRate;
}
