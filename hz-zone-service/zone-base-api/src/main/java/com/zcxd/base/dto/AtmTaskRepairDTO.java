package com.zcxd.base.dto;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 
 * @ClassName AtmTaskRepairDTO
 * @Description 维修任务
 * @author 秦江南
 * @Date 2021年6月15日下午5:50:00
 */
@Data
public class AtmTaskRepairDTO {

    /**
     * 预约时间
     */
    private Long planTime;

    /**
     * 任务内容
     */
    private String content;

    /**
     * 维修公司
     */
    private String repairCompany;

    /**
     * 业务员到达时间
     */
    private Long arriveTime;

    /**
     * 厂家到达时间
     */
    private Long engineerArriveTime;

    /**
     * 维修人
     */
    private String engineerName;

    /**
     * 故障类型
     */
    private String faultType;

    /**
     * 故障描述
     */
    private String description;

    /**
     * 备注说明
     */
    private String comments;

    /**
     * 是否更换钞箱
     */
    private Integer cashboxReplace;

    /**
     * 钞箱是否有现金
     */
    private Integer cashInBox;

    /**
     * 处理结果说明
     */
    private String dealComments;

    /**
     * 完成时间
     */
    private Long finishTime;
    
    /**
     * ATM运行状态
     */
    private Integer atmRunStatus;
    /**
     * 卡钞金额
     */
    private BigDecimal stuckAmount;

}
