package com.zcxd.base.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AtmTaskRecordDTO {

    private Long id;
	/**
     * 设备编号
     */
    private String terNo;

    /**
     * 设备所属银行
     */
    private String headBank;

    /**
     * 设备网点
     */
    private String bankName;

    /**
     * 任务列表（1,2,3,4)  对应 维护，加钞，清机，巡检
     */
    private Integer taskType;

    /**
     * 任务日期
     */
    private Long taskDate;

    /**
     * 分配线路
     */
    private String routeNo;
    
    /**
     * 加钞金额
     */
    private BigDecimal amount;
    /**
     * 卡钞金额
     */
    private BigDecimal stuckAmount;

    /**
     * 导入批次
     */
    private Long importBatch;
    
    /**
     * 任务状态
     */
    private Integer statusT;

    /**
     * 开始时间
     */
    private Long beginTime;

    /**
     * 结束时间
     */
    private Long endTime;

    /**
     * 任务备注
     */
    private String comments;

    /**
     * 巡检结果
     */
    private Integer checkResult;
	
}
