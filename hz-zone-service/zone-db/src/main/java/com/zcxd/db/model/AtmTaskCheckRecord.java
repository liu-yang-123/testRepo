package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 安全巡查记录
 * </p>
 *
 * @author admin
 * @since 2021-07-22
 */
public class AtmTaskCheckRecord extends Model<AtmTaskCheckRecord> {

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
     * 检查项目结果
     */
    private String checkItemResult;

    /**
     * 备注说明
     */
    private String comments;

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

    public String getCheckItemResult() {
        return checkItemResult;
    }

    public void setCheckItemResult(String checkItemResult) {
        this.checkItemResult = checkItemResult;
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

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "AtmTaskCheckRecord{" +
        "id=" + id +
        ", routeId=" + routeId +
        ", atmTaskId=" + atmTaskId +
        ", atmId=" + atmId +
        ", checkItemResult=" + checkItemResult +
        ", comments=" + comments +
        ", createTime=" + createTime +
        "}";
    }
}
