package com.zcxd.pda.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 网点分组atm任务列表DTO
 */
@Data
public class AtmTaskGroupItemNew {
//    private Long taskDate; //任务日期
    private Long taskId;  //任务id
    private String atmNo; //设备名称
    private String taskName; //任务类型名称
    private Integer taskType; //任务类型
    private BigDecimal amount; //加钞金额
    private Integer statusT; //任务状态
    private Integer denomValue; //券别面值
    private Integer cashBoxCnt; //钞盒数量
    private String backupFlagText; // 预排名称
    private Long repairPlanTime; //维修计划时间
    private String repairContent; //维修内容
    private String repairCompany; //维修公司名称
    private String comments; //任务备注
}
