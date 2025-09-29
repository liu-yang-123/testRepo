package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 工作日汽车尾号限行规则
 * </p>
 *
 * @author admin
 * @since 2021-07-05
 */
public class SchdDriveRestrict extends Model<SchdDriveRestrict> {

    private static final long serialVersionUID=1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 星期几
     */
    private Integer weekday;

    /**
     * 早高峰开始时间
     */
    private String beginTimeAm;

    /**
     * 早高峰结束时间
     */
    private String endTimeAm;

    /**
     * 晚高峰开始时间
     */
    private String beginTimePm;

    /**
     * 晚高峰结束时间
     */
    private String endTimePm;

    /**
     * 限行尾号
     */
    private String forbidNumbers;

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

    public Integer getWeekday() {
        return weekday;
    }

    public void setWeekday(Integer weekday) {
        this.weekday = weekday;
    }

    public String getBeginTimeAm() {
        return beginTimeAm;
    }

    public void setBeginTimeAm(String beginTimeAm) {
        this.beginTimeAm = beginTimeAm;
    }

    public String getEndTimeAm() {
        return endTimeAm;
    }

    public void setEndTimeAm(String endTimeAm) {
        this.endTimeAm = endTimeAm;
    }

    public String getBeginTimePm() {
        return beginTimePm;
    }

    public void setBeginTimePm(String beginTimePm) {
        this.beginTimePm = beginTimePm;
    }

    public String getEndTimePm() {
        return endTimePm;
    }

    public void setEndTimePm(String endTimePm) {
        this.endTimePm = endTimePm;
    }

    public String getForbidNumbers() {
        return forbidNumbers;
    }

    public void setForbidNumbers(String forbidNumbers) {
        this.forbidNumbers = forbidNumbers;
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
        return "SchdDriveRestrict{" +
        "id=" + id +
        ", weekday=" + weekday +
        ", beginTimeAm=" + beginTimeAm +
        ", endTimeAm=" + endTimeAm +
        ", beginTimePm=" + beginTimePm +
        ", endTimePm=" + endTimePm +
        ", forbidNumbers=" + forbidNumbers +
        ", createUser=" + createUser +
        ", createTime=" + createTime +
        ", updateUser=" + updateUser +
        ", updateTime=" + updateTime +
        ", deleted=" + deleted +
        "}";
    }
}
