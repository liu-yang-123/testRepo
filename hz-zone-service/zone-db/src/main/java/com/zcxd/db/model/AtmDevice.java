package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * ATM设备信息
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public class AtmDevice extends Model<AtmDevice> {

    private static final long serialVersionUID=1L;

    /**
     * 自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 终端编号
     */
    private String terNo;

    /**
     * 设备类型
     */
    private String terType;

    /**
     * 设备品牌
     */
    private String terFactory;

    /**
     * 加钞券别
     */
    private Integer denom;

    /**
     * 位置类型（离行式..)
     */
    private Integer locationType;

    /**
     * 状态
     */
    private Integer statusT;

    /**
     * 装机信息
     */
    private String installInfo;
    
    /**
     * 所属机构
     */
    private Long bankId;

    /**
     * 所属网点
     */
    private Long subBankId;
    
    /**
     * 取吞卡网点
     */
    private Long gulpBankId;

    /**
     * 备注
     */
    private String comments;

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

    public String getTerNo() {
        return terNo;
    }

    public void setTerNo(String terNo) {
        this.terNo = terNo;
    }

    public String getTerType() {
        return terType;
    }

    public void setTerType(String terType) {
        this.terType = terType;
    }

    public String getTerFactory() {
        return terFactory;
    }

    public void setTerFactory(String terFactory) {
        this.terFactory = terFactory;
    }

    public Integer getDenom() {
        return denom;
    }

    public void setDenom(Integer denom) {
        this.denom = denom;
    }

    public Integer getLocationType() {
        return locationType;
    }

    public void setLocationType(Integer locationType) {
        this.locationType = locationType;
    }

    public Integer getStatusT() {
        return statusT;
    }

    public void setStatusT(Integer statusT) {
        this.statusT = statusT;
    }

    public Long getBankId() {
        return bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public Long getSubBankId() {
        return subBankId;
    }

    public void setSubBankId(Long subBankId) {
        this.subBankId = subBankId;
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

	public String getInstallInfo() {
		return installInfo;
	}

	public void setInstallInfo(String installInfo) {
		this.installInfo = installInfo;
	}

	public Long getGulpBankId() {
		return gulpBankId;
	}

	public void setGulpBankId(Long gulpBankId) {
		this.gulpBankId = gulpBankId;
	}

	@Override
	public String toString() {
		return "AtmDevice [id=" + id + ", terNo=" + terNo + ", terType=" + terType + ", terFactory=" + terFactory
				+ ", denom=" + denom + ", locationType=" + locationType + ", statusT=" + statusT + ", installInfo="
				+ installInfo + ", bankId=" + bankId + ", subBankId=" + subBankId + ", gulpBankId=" + gulpBankId
				+ ", comments=" + comments + ", createUser=" + createUser + ", createTime=" + createTime
				+ ", updateUser=" + updateUser + ", updateTime=" + updateTime + ", deleted=" + deleted + "]";
	}

	
}