package com.zcxd.gun.db.model;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.ToString;

/**
 * @author zccc
 */
@ToString
@Data
public class GunTask extends Model<GunTask> {
    private static final long serialVersionUID=1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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
     * 调度结果记录Id
     */
    private Long schdId;

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
     * 任务状态
     */
    @TableField(exist = false)
    private String taskStatusStr;

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

    /**
     * 所属部门Id
     */
    private Integer departmentId;

    /**
     * 创建人
     */
    @TableField(fill= FieldFill.INSERT)
    private Long createUser;

    /**
     * 创建时间
     */
    @TableField(fill= FieldFill.INSERT)
    private Long createTime;

    /**
     * 更新人
     */
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Long updateUser;

    /**
     * 更新时间
     */
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Long updateTime;

    /**
     * 删除标志
     */
    @TableLogic
    private Integer deleted;
}
