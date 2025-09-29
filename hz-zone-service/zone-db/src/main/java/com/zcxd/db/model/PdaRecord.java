package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * PDA领用记录
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public class PdaRecord extends Model<PdaRecord> {

    private static final long serialVersionUID=1L;

    /**
     * 自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 终端编号
     */
    private Long terId;

    /**
     * 操作类型(借出/归还)
     */
    private Integer opType;

    /**
     * 使用人
     */
    private Integer useMan;

    /**
     * 备注
     */
    private String comments;

    /**
     * 创建时间
     */
    private Long createTime;

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

    public Long getTerId() {
        return terId;
    }

    public void setTerId(Long terId) {
        this.terId = terId;
    }

    public Integer getOpType() {
        return opType;
    }

    public void setOpType(Integer opType) {
        this.opType = opType;
    }

    public Integer getUseMan() {
        return useMan;
    }

    public void setUseMan(Integer useMan) {
        this.useMan = useMan;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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
        return "PdaRecord{" +
        "id=" + id +
        ", terId=" + terId +
        ", opType=" + opType +
        ", useMan=" + useMan +
        ", comments=" + comments +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        "}";
    }
}
