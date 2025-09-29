package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author admin
 * @since 2021-07-22
 */
public class AtmTaskImportRecord extends Model<AtmTaskImportRecord> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private Long departmentId;

    /**
     * 导入类型 0-清机任务 1-清分任务
     */
    private Integer importType;

    /**
     * 导入文件日期
     */
    private Long taskDate;

    /**
     * 银行类型
     */
    private Integer bankType;

    /**
     * 线路编号
     */
    private String routeNo;

    /**
     * 文件名称
     */
    private String fileName;
    
    /**
     * 系统文件名
     */
    private String systemFileName;
    
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

    public Integer getImportType() {
        return importType;
    }

    public void setImportType(Integer importType) {
        this.importType = importType;
    }

    public Long getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(Long taskDate) {
        this.taskDate = taskDate;
    }

    public Integer getBankType() {
        return bankType;
    }

    public void setBankType(Integer bankType) {
        this.bankType = bankType;
    }

    public String getRouteNo() {
        return routeNo;
    }

    public void setRouteNo(String routeNo) {
        this.routeNo = routeNo;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getDeleted() {
        return deleted;
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

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

	public String getSystemFileName() {
		return systemFileName;
	}

	public void setSystemFileName(String systemFileName) {
		this.systemFileName = systemFileName;
	}

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	@Override
	public String toString() {
		return "AtmTaskImportRecord [id=" + id + ", importType=" + importType + ", taskDate=" + taskDate + ", bankType="
				+ bankType + ", routeNo=" + routeNo + ", fileName=" + fileName + ", systemFileName=" + systemFileName
				+ ", createUser=" + createUser + ", createTime=" + createTime + ", updateUser=" + updateUser
				+ ", updateTime=" + updateTime + ", deleted=" + deleted + "]";
	}

}
