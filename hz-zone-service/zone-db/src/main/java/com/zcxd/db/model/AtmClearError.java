package com.zcxd.db.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * atm清分差错明细
 * </p>
 *
 * @author admin
 * @since 2021-07-24
 */
public class AtmClearError extends Model<AtmClearError> {

    private static final long serialVersionUID=1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 出入库单号
     */
    private Long taskId;

    /**
     * 对应设备
     */
    private Long atmId;

    /**
     * 差错明细类型: 1 - 假币，2 - 残缺币，3 - 夹张
     */
    private Integer detailType;

    /**
     * 券别
     */
    private Long denomId;

    /**
     * 张数
     */
    private Integer count;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 冠字号
     */
    private String rmbSn;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 逻辑删除
     */
    private Integer deleted;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getAtmId() {
        return atmId;
    }

    public void setAtmId(Long atmId) {
        this.atmId = atmId;
    }

    public Integer getDetailType() {
        return detailType;
    }

    public void setDetailType(Integer detailType) {
        this.detailType = detailType;
    }

    public Long getDenomId() {
        return denomId;
    }

    public void setDenomId(Long denomId) {
        this.denomId = denomId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getRmbSn() {
        return rmbSn;
    }

    public void setRmbSn(String rmbSn) {
        this.rmbSn = rmbSn;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
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
        return "AtmClearError{" +
        "id=" + id +
        ", taskId=" + taskId +
        ", atmId=" + atmId +
        ", detailType=" + detailType +
        ", denomId=" + denomId +
        ", count=" + count +
        ", amount=" + amount +
        ", rmbSn=" + rmbSn +
        ", createTime=" + createTime +
        ", deleted=" + deleted +
        "}";
    }
}
