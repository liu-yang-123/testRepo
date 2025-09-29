package com.zcxd.db.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;

/**
 * <p>
 * 金库库存
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public class VaultVolum extends Model<VaultVolum> {

    private static final long serialVersionUID=1L;

    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 部门ID
     */
    private Long departmentId;

    /**
     * 银行机构
     */
    private Long bankId;

    /**
     * 库存类型:可用券，残损券，五好券
     */
    private Integer denomType;

    /**
     * 券别
     */
    private Long denomId;

    /**
     * 金额
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal amount;

    /**
     * 张数
     */
    private Long count;

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


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getBankId() {
        return bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public Integer getDenomType() {
        return denomType;
    }

    public void setDenomType(Integer denomType) {
        this.denomType = denomType;
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

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "VaultVolum{" +
        "id=" + id +
        ", bankId=" + bankId +
        ", volumType=" + denomType +
        ", denomId=" + denomId +
        ", amount=" + amount +
        ", createUser=" + createUser +
        ", createTime=" + createTime +
        ", updateUser=" + updateUser +
        ", updateTime=" + updateTime +
        "}";
    }
}
