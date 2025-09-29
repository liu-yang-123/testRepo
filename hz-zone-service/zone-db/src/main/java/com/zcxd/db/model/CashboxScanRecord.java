package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 钞盒扫描记录
 * </p>
 *
 * @author admin
 * @since 2021-07-26
 */
public class CashboxScanRecord extends Model<CashboxScanRecord> {

    private static final long serialVersionUID=1L;

    /**
     * 自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 包装记录id
     */
    private Long packId;

    /**
     * 扫描节点
     */
    private Integer scanNode;




    /**
     * 扫描人
     */
    private Long scanUser;
    /**
     * 扫描时间
     */
    private Long scanTime;
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

    public Long getPackId() {
        return packId;
    }

    public void setPackId(Long packId) {
        this.packId = packId;
    }

    public Integer getScanNode() {
        return scanNode;
    }

    public void setScanNode(Integer scanNode) {
        this.scanNode = scanNode;
    }

    public Long getScanUser() {
        return scanUser;
    }

    public void setScanUser(Long scanUser) {
        this.scanUser = scanUser;
    }

    public Long getScanTime() {
        return scanTime;
    }

    public void setScanTime(Long scanTime) {
        this.scanTime = scanTime;
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
        return "CashboxScanRecord{" +
                "id=" + id +
                ", packId=" + packId +
                ", scanNode=" + scanNode +
                ", scanUser=" + scanUser +
                ", scanTime=" + scanTime +
                ", createTime=" + createTime +
                '}';
    }
}
