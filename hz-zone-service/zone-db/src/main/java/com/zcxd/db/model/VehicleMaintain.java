package com.zcxd.db.model;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 车辆维保记录
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public class VehicleMaintain extends Model<VehicleMaintain> {

    private static final long serialVersionUID=1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 设备编号
     */
    private Long vehicleId;
    
    /**
     * 所属顶级部门
     */
    private Long departmentId;

    /**
     * 维保日期
     */
    private Long mtDate;

    /**
     * 维保类型
     */
    private String mtType;

    /**
     * 故障原因
     */
    private String mtReason;

    /**
     * 维保内容
     */
    private String mtContent;

    /**
     * 维保成本
     */
    private BigDecimal mtCost;

    /**
     * 维保结果: 1 - 维修成功，0- 维修失败
     */
    private Integer mtResult;

    /**
     * 经办人
     */
    private Integer mtEmployee;

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

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getMtDate() {
        return mtDate;
    }

    public void setMtDate(Long mtDate) {
        this.mtDate = mtDate;
    }

    public String getMtType() {
        return mtType;
    }

    public void setMtType(String mtType) {
        this.mtType = mtType;
    }

    public String getMtReason() {
        return mtReason;
    }

    public void setMtReason(String mtReason) {
        this.mtReason = mtReason;
    }

    public String getMtContent() {
        return mtContent;
    }

    public void setMtContent(String mtContent) {
        this.mtContent = mtContent;
    }

    public BigDecimal getMtCost() {
        return mtCost;
    }

    public void setMtCost(BigDecimal mtCost) {
        this.mtCost = mtCost;
    }

    public Integer getMtResult() {
        return mtResult;
    }

    public void setMtResult(Integer mtResult) {
        this.mtResult = mtResult;
    }

    public Integer getMtEmployee() {
        return mtEmployee;
    }

    public void setMtEmployee(Integer mtEmployee) {
        this.mtEmployee = mtEmployee;
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
		return "VehicleMaintain [id=" + id + ", vehicleId=" + vehicleId + ", departmentId=" + departmentId + ", mtDate="
				+ mtDate + ", mtType=" + mtType + ", mtReason=" + mtReason + ", mtContent=" + mtContent + ", mtCost="
				+ mtCost + ", mtResult=" + mtResult + ", mtEmployee=" + mtEmployee + ", createUser=" + createUser
				+ ", createTime=" + createTime + ", updateUser=" + updateUser + ", updateTime=" + updateTime
				+ ", deleted=" + deleted + "]";
	}

}
