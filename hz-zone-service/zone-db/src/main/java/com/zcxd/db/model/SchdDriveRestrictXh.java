package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 假期西湖限行规则
 * </p>
 *
 * @author admin
 * @since 2021-07-05
 */
public class SchdDriveRestrictXh extends Model<SchdDriveRestrictXh> {

    private static final long serialVersionUID=1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 单双日（0 - 双日，1 - 单日）
     */
    private Integer dayType;

    /**
     * 开始时间
     */
    private String beginTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 允许尾号
     */
    private String permitNumbers;

    /**
     * 影响线路组
     */
    private String effectRoutes;

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
     * 所属部门
     */
    private Long departmentId;
    /**
     * 删除标志
     */
    private Integer deleted;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDayType() {
        return dayType;
    }

    public void setDayType(Integer dayType) {
        this.dayType = dayType;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getPermitNumbers() {
        return permitNumbers;
    }

    public void setPermitNumbers(String permitNumbers) {
        this.permitNumbers = permitNumbers;
    }

    public String getEffectRoutes() {
        return effectRoutes;
    }

    public void setEffectRoutes(String effectRoutes) {
        this.effectRoutes = effectRoutes;
    }

    public Long getCreateUser() {
        return createUser;
    }

    public void setCreateUser(Long createUser) {
        this.createUser = createUser;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(Long updateUser) {
        this.updateUser = updateUser;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "SchdDriveRestrictXh{" +
        "id=" + id +
        ", dayType=" + dayType +
        ", beginTime=" + beginTime +
        ", endTime=" + endTime +
        ", permitNumbers=" + permitNumbers +
        ", effectRoutes=" + effectRoutes +
        ", createUser=" + createUser +
        ", createTime=" + createTime +
        ", updateUser=" + updateUser +
        ", updateTime=" + updateTime +
        ", deleted=" + deleted +
        "}";
    }
}
