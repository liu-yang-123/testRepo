package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 交通限行规则
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public class TrafficRule extends Model<TrafficRule> {

    private static final long serialVersionUID=1L;

    /**
     * 自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 限行尾号
     */
    private String limitNo;

    /**
     * 星期
     */
    private Integer weekDay;

    /**
     * 规则类型（0普通,1-特殊)
     */
    private Integer limitType;

    /**
     * 特殊日期
     */
    private String specDay;

    /**
     * 特殊日期限行
     */
    private Integer specAction;

    /**
     * 备注
     */
    private String comments;

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

    public String getLimitNo() {
        return limitNo;
    }

    public void setLimitNo(String limitNo) {
        this.limitNo = limitNo;
    }

    public Integer getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(Integer weekDay) {
        this.weekDay = weekDay;
    }

    public Integer getLimitType() {
        return limitType;
    }

    public void setLimitType(Integer limitType) {
        this.limitType = limitType;
    }

    public String getSpecDay() {
        return specDay;
    }

    public void setSpecDay(String specDay) {
        this.specDay = specDay;
    }

    public Integer getSpecAction() {
        return specAction;
    }

    public void setSpecAction(Integer specAction) {
        this.specAction = specAction;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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
        return "TrafficRule{" +
        "id=" + id +
        ", limitNo=" + limitNo +
        ", weekDay=" + weekDay +
        ", limitType=" + limitType +
        ", specDay=" + specDay +
        ", specAction=" + specAction +
        ", comments=" + comments +
        ", createUser=" + createUser +
        ", createTime=" + createTime +
        ", updateUser=" + updateUser +
        ", updateTime=" + updateTime +
        ", deleted=" + deleted +
        "}";
    }
}
