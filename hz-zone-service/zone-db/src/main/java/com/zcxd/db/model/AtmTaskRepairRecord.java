package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 设备维护维修操作记录
 * </p>
 *
 * @author admin
 * @since 2021-07-22
 */
public class AtmTaskRepairRecord extends Model<AtmTaskRepairRecord> {

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
     * 设备任务id
     */
    private Long atmTaskId;

    /**
     * 设备id
     */
    private Long atmId;

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
     * 处理结果( 1 - 修复，0 - 未修复）
     */
    private Integer dealResult;

    /**
     * 处理结果说明
     */
    private String dealComments;

    /**
     * 完成时间
     */
    private Long finishTime;

    /**
     * 创建时间
     */
    @TableField(fill= FieldFill.INSERT)
    private Long createTime;


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

    public Long getAtmTaskId() {
        return atmTaskId;
    }

    public void setAtmTaskId(Long atmTaskId) {
        this.atmTaskId = atmTaskId;
    }

    public Long getAtmId() {
        return atmId;
    }

    public void setAtmId(Long atmId) {
        this.atmId = atmId;
    }

    public Long getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(Long arriveTime) {
        this.arriveTime = arriveTime;
    }

    public Long getEngineerArriveTime() {
        return engineerArriveTime;
    }

    public void setEngineerArriveTime(Long engineerArriveTime) {
        this.engineerArriveTime = engineerArriveTime;
    }

    public String getEngineerName() {
        return engineerName;
    }

    public void setEngineerName(String engineerName) {
        this.engineerName = engineerName;
    }

    public String getFaultType() {
        return faultType;
    }

    public void setFaultType(String faultType) {
        this.faultType = faultType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getCashboxReplace() {
        return cashboxReplace;
    }

    public void setCashboxReplace(Integer cashboxReplace) {
        this.cashboxReplace = cashboxReplace;
    }

    public Integer getCashInBox() {
        return cashInBox;
    }

    public void setCashInBox(Integer cashInBox) {
        this.cashInBox = cashInBox;
    }

    public Integer getDealResult() {
        return dealResult;
    }

    public void setDealResult(Integer dealResult) {
        this.dealResult = dealResult;
    }

    public String getDealComments() {
        return dealComments;
    }

    public void setDealComments(String dealComments) {
        this.dealComments = dealComments;
    }

    public Long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Long finishTime) {
        this.finishTime = finishTime;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "AtmTaskRepairRecord{" +
        "id=" + id +
        ", routeId=" + routeId +
        ", atmTaskId=" + atmTaskId +
        ", atmId=" + atmId +
        ", arriveTime=" + arriveTime +
        ", engineerArriveTime=" + engineerArriveTime +
        ", engineerName=" + engineerName +
        ", faultType=" + faultType +
        ", description=" + description +
        ", comments=" + comments +
        ", cashboxReplace=" + cashboxReplace +
        ", cashInBox=" + cashInBox +
        ", dealComments=" + dealComments +
        ", finishTime=" + finishTime +
        ", createTime=" + createTime +
        "}";
    }
}
