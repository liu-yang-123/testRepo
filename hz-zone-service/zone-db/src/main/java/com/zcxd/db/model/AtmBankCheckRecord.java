package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 安全巡查子任务表
 * </p>
 *
 * @author admin
 * @since 2021-06-09
 */
public class AtmBankCheckRecord extends Model<AtmBankCheckRecord> {

    private static final long serialVersionUID=1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 线路id
     */
    private Long routeId;

    /**
     * 所属银行
     */
    private Long bankId;

    /**
     * 设备网点
     */
    private Long subBankId;

    /**
     * 巡检人
     */
    private String checkMans;

    /**
     * 检查时间
     */
    private Long checkTime;

    /**
     * 加钞间检查结果
     */
    private String roomCheckResult;

    /**
     * 取款前厅检查结果（json,  字段：0/1  0 - 不正常，1正常）
     */
    private String hallCheckResult;

    private Integer checkNormal; //检查结果是否正常
    /**
     * 备注说明
     */
    private String comments;

    /**
     * 撤防时间
     */
    private Long revokeAlarmTime;

    /**
     * 布放时间
     */
    private Long setAlarmTime;

    private Long departmentId;
    /**
     * 创建时间
     */
    @TableField(fill= FieldFill.INSERT)
    private Long createTime;

    /**
     * 更新时间
     */
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Long updateTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }


    public Long getBankId() {
        return bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public Long getSubBankId() {
        return subBankId;
    }

    public void setSubBankId(Long subBankId) {
        this.subBankId = subBankId;
    }

    public String getCheckMans() {
        return checkMans;
    }

    public void setCheckMans(String checkMans) {
        this.checkMans = checkMans;
    }

    public Long getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Long checkTime) {
        this.checkTime = checkTime;
    }

    public String getRoomCheckResult() {
        return roomCheckResult;
    }

    public void setRoomCheckResult(String roomCheckResult) {
        this.roomCheckResult = roomCheckResult;
    }

    public String getHallCheckResult() {
        return hallCheckResult;
    }

    public void setHallCheckResult(String hallCheckResult) {
        this.hallCheckResult = hallCheckResult;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getCheckNormal() {
        return checkNormal;
    }

    public void setCheckNormal(Integer checkNormal) {
        this.checkNormal = checkNormal;
    }

    public Long getRevokeAlarmTime() {
        return revokeAlarmTime;
    }

    public void setRevokeAlarmTime(Long revokeAlarmTime) {
        this.revokeAlarmTime = revokeAlarmTime;
    }

    public Long getSetAlarmTime() {
        return setAlarmTime;
    }

    public void setSetAlarmTime(Long setAlarmTime) {
        this.setAlarmTime = setAlarmTime;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "AtmBankCheckRecord{" +
        "id=" + id +
        ", routeId=" + routeId +
        ", bankId=" + bankId +
        ", subBankId=" + subBankId +
        ", checkTime=" + checkTime +
        ", roomCheckResult=" + roomCheckResult +
        ", hallCheckResult=" + hallCheckResult +
        ", comments=" + comments +
        ", revokeAlarmTime=" + revokeAlarmTime +
        ", setAlarmTime=" + setAlarmTime +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        "}";
    }
}
