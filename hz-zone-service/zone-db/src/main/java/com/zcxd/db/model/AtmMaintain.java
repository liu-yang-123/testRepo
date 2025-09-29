package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * ATM维修任务
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public class AtmMaintain extends Model<AtmMaintain> {

    private static final long serialVersionUID=1L;

    /**
     * 自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 终端ID
     */
    private String amtId;

    /**
     * 任务编号
     */
    private String taskNo;

    /**
     * 任务类型
     */
    private String taskType;

    /**
     * 任务备注
     */
    private String comments;

    /**
     * 故障列表
     */
    private String issueList;

    /**
     * 任务状态
     */
    private String status;

    /**
     * 分配线路
     */
    private Long lineId;

    /**
     * 任务日期
     */
    private Long taskDate;

    /**
     * 开始时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;

    /**
     * 创建人
     */
    private Long createUser;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新人
     */
    private Long updateUser;

    /**
     * 更新时间
     */
    private Long updateTime;

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

    public String getAmtId() {
        return amtId;
    }

    public void setAmtId(String amtId) {
        this.amtId = amtId;
    }

    public String getTaskNo() {
        return taskNo;
    }

    public void setTaskNo(String taskNo) {
        this.taskNo = taskNo;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getIssueList() {
        return issueList;
    }

    public void setIssueList(String issueList) {
        this.issueList = issueList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getLineId() {
        return lineId;
    }

    public void setLineId(Long lineId) {
        this.lineId = lineId;
    }

    public Long getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(Long taskDate) {
        this.taskDate = taskDate;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
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
        return "AtmMaintain{" +
        "id=" + id +
        ", amtId=" + amtId +
        ", taskNo=" + taskNo +
        ", taskType=" + taskType +
        ", comments=" + comments +
        ", issueList=" + issueList +
        ", status=" + status +
        ", lineId=" + lineId +
        ", taskDate=" + taskDate +
        ", startTime=" + startTime +
        ", endTime=" + endTime +
        ", createUser=" + createUser +
        ", createTime=" + createTime +
        ", updateUser=" + updateUser +
        ", updateTime=" + updateTime +
        ", deleted=" + deleted +
        "}";
    }
}
