package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * PDA终端信息
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public class Pda extends Model<Pda> {

    private static final long serialVersionUID=1L;

    /**
     * 自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 终端编号
     */
    private String tersn;

    /**
     * 物理地址
     */
    private String mac;

    /**
     * 用途(尾箱，清机、库管）
     */
    private Integer useType;
    
    /**
     * 所属顶级部门
     */
    private Long departmentId;

    /**
     * 所属机构
     */
    private Long bankId;

    /**
     * 备注
     */
    private String comments;

    /**
     * 状态
     */
    private Integer statusT;

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

    public String getTersn() {
        return tersn;
    }

    public void setTersn(String tersn) {
        this.tersn = tersn;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public Integer getUseType() {
        return useType;
    }

    public void setUseType(Integer useType) {
        this.useType = useType;
    }

    public Long getBankId() {
        return bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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
    
    public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	@Override
	public String toString() {
		return "Pda [id=" + id + ", tersn=" + tersn + ", mac=" + mac + ", useType=" + useType + ", departmentId="
				+ departmentId + ", bankId=" + bankId + ", comments=" + comments + ", statusT=" + statusT
				+ ", createUser=" + createUser + ", createTime=" + createTime + ", updateUser=" + updateUser
				+ ", updateTime=" + updateTime + ", deleted=" + deleted + "]";
	}

}
