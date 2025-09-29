package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 箱包记录
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public class BoxpackRecord extends Model<BoxpackRecord> {

    private static final long serialVersionUID=1L;

    /**
     * 自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务编号
     */
    private Long taskId;

    /**
     * 箱包id
     */
    private Integer bpkId;

    /**
     * 金库交接时间
     */
    private Long warehouseTime;

    /**
     * 银行交接时间
     */
    private Long bankTime;


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

    public Integer getBpkId() {
        return bpkId;
    }

    public void setBpkId(Integer bpkId) {
        this.bpkId = bpkId;
    }

    public Long getWarehouseTime() {
        return warehouseTime;
    }

    public void setWarehouseTime(Long warehouseTime) {
        this.warehouseTime = warehouseTime;
    }

    public Long getBankTime() {
        return bankTime;
    }

    public void setBankTime(Long bankTime) {
        this.bankTime = bankTime;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "BoxpackRecord{" +
        "id=" + id +
        ", taskId=" + taskId +
        ", bpkId=" + bpkId +
        ", warehouseTime=" + warehouseTime +
        ", bankTime=" + bankTime +
        "}";
    }
}
