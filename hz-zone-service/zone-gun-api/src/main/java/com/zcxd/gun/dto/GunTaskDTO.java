package com.zcxd.gun.dto;

import lombok.Data;

/**
 * @author zccc
 */
@Data
public class GunTaskDTO {

    /**
     * 押运员Aid
     */
    private Long supercargoIdA;

    /**
     * 押运员A姓名
     */
    private String supercargoNameA;

    /**
     * 枪支编号A
     */
    private String gunCodeA;

    /**
     * 弹盒A
     */
    private String gunBoxCodeA;

    /**
     * 枪证编号A
     */
    private String gunLicenceNumA;

    /**
     * 枪证A（发枪）状态
     * 1：未发
     * 2：已发
     */
    private Integer gunLicenseOutA;

    /**
     * 枪证A（收枪）状态
     * 1：未收
     * 2：已收
     */
    private Integer gunLicenseInA;

    /**
     * 押运员Bid
     */
    private Long supercargoIdB;

    /**
     * 押运员B姓名
     */
    private String supercargoNameB;

    /**
     * 枪支编号B
     */
    private String gunCodeB;

    /**
     * 弹盒B
     */
    private String gunBoxCodeB;

    /**
     * 枪证编号B
     */
    private String gunLicenceNumB;

    /**
     * 枪证B（发枪）状态
     * 1：未发
     * 2：已发
     */
    private Integer gunLicenseOutB;

    /**
     * 枪证B（收枪）状态
     * 1：未收
     * 2：已收
     */
    private Integer gunLicenseInB;

    /**
     * 线路id
     */
    private Integer lineId;

    /**
     * 线路名称
     */
    private String lineName;

    /**
     * 车辆号码
     */
    private String carNo;

    /**
     * 计划发枪时间
     */
    private Long planTime;

    /**
     * 任务状态
     */
    private Integer taskStatus;

    /**
     * 实际发枪时间
     */
    private Long takeOutTime;

    /**
     * 收枪时间
     */
    private Long takeInTime;

    /**
     * 枪证未返还备注
     */
    private String gunLiNotReturnRemark;

    /**
     * 线路审核员
     */
    private String lineAuditor;

    /**
     * 操作员
     */
    private String operatorName;
}
