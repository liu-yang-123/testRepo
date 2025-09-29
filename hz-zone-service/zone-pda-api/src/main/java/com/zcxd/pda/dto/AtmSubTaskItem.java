package com.zcxd.pda.dto;

import lombok.Data;

/**
 * atm子任务项信息
 */
@Data
public class AtmSubTaskItem {
    /**
     * 子任务id
     */
    private Long subTaskId;

    /**
     * 子任务类型
     */
    private Integer taskType;

    /**
     * 子任务类型名称
     */
    private String taskTypeText;

    /**
     * 子任务内容
     */
    private String taskContent;
    /**
     * 加钞券别面额
     */
    private Integer denomValue;
    /**
     * 任务状态
     */
    private Integer statusT;
    /**
     * 预约时间
     */
    private Long planTime;
}
