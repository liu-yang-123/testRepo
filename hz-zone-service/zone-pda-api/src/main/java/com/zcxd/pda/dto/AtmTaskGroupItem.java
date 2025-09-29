package com.zcxd.pda.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 网点分组atm任务列表DTO
 */
@Data
public class AtmTaskGroupItem {
    /**
     * 任务id
     */
    private Long taskId;
    /**
     * 设备名称
     */
    private String atmNo;

    /**
     * 任务类型名称
     */
    private String taskName;

    /**
     * 任务状态
     */
    private Integer statusT;

    /**
     * 是否离行式( 1 - 离行式，0 - 其他）
     */
    private Integer atmLocateType;

    private String backupFlagText;

    private String taskContent;
    private String comments;
}
