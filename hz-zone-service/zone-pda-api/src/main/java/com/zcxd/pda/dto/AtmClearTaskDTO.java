package com.zcxd.pda.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * atm任务详情DTO
 */
@Data
public class AtmClearTaskDTO {

    /**
     * 清分任务日期
     */
    private String taskDate;
    /**
     * ATM设备编号
     */
    private String atmNo;
    /**
     * 银行id
     */
    private Long bankId;
    /**
     * 银行名称
     */
    private String bankName;
    /**
     * 线路编号名称
     */
    private String routeNo;
    /**
     * 线路id
     */
    private Long routeId;

    /**
     * 清机任务id
     */
    private Long taskId;
    /**
     * 回笼钞袋/钞盒标签
     */
    private List<String> retBoxList;

    /**
     * atm当日所有清分任务
     */
    private List<AtmClearTaskRecord> taskRecordList;
}
