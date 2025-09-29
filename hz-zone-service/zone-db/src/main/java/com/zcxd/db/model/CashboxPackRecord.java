package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 钞盒与清分员关联表
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public class CashboxPackRecord extends Model<CashboxPackRecord> {

    private static final long serialVersionUID=1L;

    /**
     * 自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 装盒时间（清分扫码时间）
     */
    private Long packTime;

    /**
     * 确认时间
     */
    private Long taskDate;
    /**
     * 清分员
     */
    private Long clearManId;

    /**
     * 复核员
     */
    private Long checkManId;

    /**
     *  交接点收清点人
     */
    private Long recvClearManId;

    /**
     *  交接点收复核人
     */
    private Long recvCheckManId;

    /**
     * 所属银行
     */
    private Long bankId;

    /**
     * 钞盒
     */
    private String boxNo;

    /**
     * 清分机
     */
    private Long devId;

    /**
     * 券别id
     */
    private Long denomId;

    /**
     * 包装金额
     */
    private BigDecimal amount;

    /**
     * 钞盒状态
     */
    private Integer statusT;
    /**
     * 钞盒使用计数（0 未使用，1 使用半盒，2 - 使用整盒
     */
    private Integer useCount;
    /**
     * 分配线路
     */
    private Long routeId;

    /**
     * 加钞设备1
     */
    private Long atmId;
    /**
     * 加钞设备2
     */
    private Long secondAtmId;

    /**
     * 部门ID
     */
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

    public Long getPackTime() {
        return packTime;
    }

    public void setPackTime(Long packTime) {
        this.packTime = packTime;
    }

    public Long getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(Long taskDate) {
        this.taskDate = taskDate;
    }

    public Long getClearManId() {
        return clearManId;
    }

    public void setClearManId(Long clearManId) {
        this.clearManId = clearManId;
    }

    public Long getCheckManId() {
        return checkManId;
    }

    public void setCheckManId(Long checkManId) {
        this.checkManId = checkManId;
    }

    public Long getRecvClearManId() {
        return recvClearManId;
    }

    public void setRecvClearManId(Long recvClearManId) {
        this.recvClearManId = recvClearManId;
    }

    public Long getRecvCheckManId() {
        return recvCheckManId;
    }

    public void setRecvCheckManId(Long recvCheckManId) {
        this.recvCheckManId = recvCheckManId;
    }

    public Long getBankId() {
        return bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public String getBoxNo() {
        return boxNo;
    }

    public void setBoxNo(String boxNo) {
        this.boxNo = boxNo;
    }

    public Long getDevId() {
        return devId;
    }

    public void setDevId(Long devId) {
        this.devId = devId;
    }

    public Long getDenomId() {
        return denomId;
    }

    public void setDenomId(Long denomId) {
        this.denomId = denomId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getStatusT() {
        return statusT;
    }

    public void setStatusT(Integer statusT) {
        this.statusT = statusT;
    }

    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Long getAtmId() {
        return atmId;
    }

    public void setAtmId(Long atmId) {
        this.atmId = atmId;
    }

    public Long getSecondAtmId() {
        return secondAtmId;
    }

    public void setSecondAtmId(Long secondAtmId) {
        this.secondAtmId = secondAtmId;
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
        return "CashboxPackRecord{" +
                "id=" + id +
                ", packTime=" + packTime +
                ", taskDate=" + taskDate +
                ", clearManId=" + clearManId +
                ", checkManId=" + checkManId +
                ", bankId=" + bankId +
                ", boxNo='" + boxNo + '\'' +
                ", devId=" + devId +
                ", denomId=" + denomId +
                ", amount=" + amount +
                ", statusT=" + statusT +
                ", routeId=" + routeId +
                ", atmId=" + atmId +
                ", secondAtmId=" + secondAtmId +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", deleted=" + deleted +
                '}';
    }
}
