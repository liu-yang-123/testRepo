package com.zcxd.pda.dto;

import lombok.Data;

import java.util.List;

/**
 * atm任务详情DTO
 */
@Data
public class AtmClearTaskRouteDTO {

    /**
     * 清分任务日期
     */
    private String taskDate;
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
     * atm当日所有清分任务
     */
    private List<AtmClearTaskRecord> taskRecordList;
}
