package com.zcxd.db.model;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

/**
 * <p>
 * 
 * </p>
 *
 * @author admin
 * @since 2021-06-18
 */
public class RouteTemplateAtm extends Model<RouteTemplateAtm> {

    private static final long serialVersionUID=1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 线路模板id
     */
    private Long routeTemplateId;

    /**
     * 设备id
     */
    private Long atmId;
    
    /**
     * 所属机构
     */
    private Long bankId;

    /**
     * 所属网点
     */
    private Long subBankId;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建人
     */
    @TableField(fill= FieldFill.INSERT)
    private Long createUser;

    /**
     * 创建时间
     */
    @TableField(fill= FieldFill.INSERT)
    private Long createTime;

    /**
     * 更新人
     */
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Long updateUser;

    /**
     * 更新时间
     */
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Long updateTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRouteTemplateId() {
        return routeTemplateId;
    }

    public void setRouteTemplateId(Long routeTemplateId) {
        this.routeTemplateId = routeTemplateId;
    }

    public Long getAtmId() {
        return atmId;
    }

    public void setAtmId(Long atmId) {
        this.atmId = atmId;
    }

    public Long getSubBankId() {
        return subBankId;
    }

    public void setSubBankId(Long subBankId) {
        this.subBankId = subBankId;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
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
    
    public Long getBankId() {
		return bankId;
	}

	public void setBankId(Long bankId) {
		this.bankId = bankId;
	}

	@Override
    protected Serializable pkVal() {
        return this.id;
    }

	@Override
	public String toString() {
		return "RouteTemplateAtm [id=" + id + ", routeTemplateId=" + routeTemplateId + ", atmId=" + atmId + ", bankId="
				+ bankId + ", subBankId=" + subBankId + ", sort=" + sort + ", createUser=" + createUser
				+ ", createTime=" + createTime + ", updateUser=" + updateUser + ", updateTime=" + updateTime + "]";
	}

}
