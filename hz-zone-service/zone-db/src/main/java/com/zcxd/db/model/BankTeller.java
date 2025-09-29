package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 银行员工信息
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public class BankTeller extends Model<BankTeller> {

    private static final long serialVersionUID=1L;

    /**
     * 自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属机构
     */
    private Long bankId;
    
    /**
     * 部门（事业部id）
     */
    private Long departmentId;

    /**
     * 柜员编号
     */
    private String tellerNo;

    /**
     * 柜员姓名
     */
    private String tellerName;

    /**
     * 联系电话
     */
    private String mobile;

    /**
     * 密码
     */
    private String password;

    /**
     * 管理员标志（0 - 一般柜员，2 - 管理员）
     */
    private Integer managerFlag;
    /**
     * 状态
     */
    private Integer statusT;
    
    /**
     * 员工id
     */
    private Long empId;

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

    public Long getBankId() {
        return bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public String getTellerNo() {
        return tellerNo;
    }

    public void setTellerNo(String tellerNo) {
        this.tellerNo = tellerNo;
    }

    public String getTellerName() {
        return tellerName;
    }

    public void setTellerName(String tellerName) {
        this.tellerName = tellerName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getManagerFlag() {
        return managerFlag;
    }

    public void setManagerFlag(Integer managerFlag) {
        this.managerFlag = managerFlag;
    }

    public Integer getStatusT() {
        return statusT;
    }

    public void setStatusT(Integer statusT) {
        this.statusT = statusT;
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
    

    public Long getEmpId() {
		return empId;
	}

	public void setEmpId(Long empId) {
		this.empId = empId;
	}
	
	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	@Override
	public String toString() {
		return "BankTeller [id=" + id + ", bankId=" + bankId + ", departmentId=" + departmentId + ", tellerNo="
				+ tellerNo + ", tellerName=" + tellerName + ", mobile=" + mobile + ", password=" + password
				+ ", managerFlag=" + managerFlag + ", statusT=" + statusT + ", empId=" + empId + ", comments="
				+ comments + ", createUser=" + createUser + ", createTime=" + createTime + ", updateUser=" + updateUser
				+ ", updateTime=" + updateTime + ", deleted=" + deleted + "]";
	}

}
