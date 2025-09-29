package com.zcxd.pda.dto;

import io.swagger.models.auth.In;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * atm任务详情DTO
 */
@Data
public class AtmTaskDetailDTO {

    /**
     * 任务id
     */
    private Long taskId;
    /**
     * ATM设备编号
     */
    private String atmNo;
    /**
     * 银行称
     */
    private String bankName;
    /**
     * 网点名称
     */
    private String subBankName;

    private Integer taskType;
    private String taskTypeText;
    /**
     * 任务日期
     */
    private Long taskDate;
    /**
     * 任务状态
     */
    private Integer statusT;
    /**
     * 是否离行式( 1 - 离行式，0 - 其他）
     */
    private Integer atmLocateType;
    /**
     * 回笼钞盒/钞袋方式
     */
    private Integer carryType;

    /**
     * 加钞金额
     */
    private BigDecimal amount; //加钞金额
    private Integer denomValue; //券别面值
    private Integer cashBoxCnt; //钞盒数量
    private String backupFlagText; //

    /**
     * 维修计划时间
     */
    private Long repairPlanTime;

    /**
     * 维修内容
     */
    private String repairContent;

    /**
     * 维修公司名称
     */
    private String repairCompany;

    /**
     * 任务备注
     */
    private String comments;
}
