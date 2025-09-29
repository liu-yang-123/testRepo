package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 部门信息
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public class Department extends Model<Department> {

    private static final long serialVersionUID=1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 部门描述
     */
    private String description;

    /**
     * 上级部门
     */
    private String parentIds;

    /**
     * 负责人姓名
     */
    private String linkmanName;

    /**
     * 联系电话
     */
    private String linkmanMobile;

    /**
     * 部门状态
     */
    private Integer statusT;

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
     * 是否删除
     */
    private Integer deleted;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParentIds() {
        return parentIds;
    }

    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }

    public String getLinkmanName() {
        return linkmanName;
    }

    public void setLinkmanName(String linkmanName) {
        this.linkmanName = linkmanName;
    }

    public String getLinkmanMobile() {
        return linkmanMobile;
    }

    public void setLinkmanMobile(String linkmanMobile) {
        this.linkmanMobile = linkmanMobile;
    }

    public Integer getStatusT() {
        return statusT;
    }

    public void setStatusT(Integer statusT) {
        this.statusT = statusT;
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
        return "Department{" +
        "id=" + id +
        ", name=" + name +
        ", description=" + description +
        ", parentIds=" + parentIds +
        ", linkmanName=" + linkmanName +
        ", linkmanMobile=" + linkmanMobile +
        ", statusT=" + statusT +
        ", createUser=" + createUser +
        ", createTime=" + createTime +
        ", updateUser=" + updateUser +
        ", updateTime=" + updateTime +
        ", deleted=" + deleted +
        "}";
    }
}
