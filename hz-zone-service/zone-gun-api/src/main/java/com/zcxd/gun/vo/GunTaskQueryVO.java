package com.zcxd.gun.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zccc
 */
@Data
public class GunTaskQueryVO {
    private Long id;
    /**
     * 押运员工号
     */
    private String supercargoNum;

    /**
     * 押运员ID
     * 用于通过工号查询时将工号转换成押运员ID
     */
    private Long supercargoId;

    /**
     * 押运员姓名
     */
    private String supercargoName;

    /**
     * 枪弹编号
     */
    private String gunCode;

    /**
     * 线路名称
     */
    private String lineName;

    /**
     * 计划发枪时间
     */
    private String taskTime;

    /**
     * 计划发枪时间范围开始
     */
    private String taskTimeStart;

    /**
     * 计划发枪时间范围结束
     */
    private String taskTimeEnd;

    /**
     * 任务状态
     */
    private String taskStatus;

    /**
     * 所属部门Id
     */
    @NotBlank(message = "部门ID不能为空")
    private Integer departmentId;
}
