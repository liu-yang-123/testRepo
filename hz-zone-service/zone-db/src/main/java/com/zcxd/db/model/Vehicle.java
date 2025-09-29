package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 车辆信息
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public class Vehicle extends Model<Vehicle> {

    private static final long serialVersionUID=1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 车牌号码
     */
    private String lpno;

    /**
     * 车辆编号
     */
    private String seqno;

    /**
     * 所属顶级部门
     */
    private Long departmentId;
    
    /**
     * 制造商
     */
    private String factory;

    /**
     * 型号
     */
    private String model;

    /**
     * 车辆种类
     */
    private Integer vhType;

    /**
     * 车辆状态
     */
    private Integer statusT;

    /**
     * 购买日期
     */
    private Long enrollDate;
    
    /**
     * 车辆类型
     */
    private String vehicleType;
    
    /**
     * 车架号
     */
    private String frameNumber;
    
    /**
     * 发动机号
     */
    private String engineNumber;
    
    /**
     * 排放标准
     */
    private String emissionStandard;
    
    /**
     * 出厂日期
     */
    private Long productionDate;
    

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

    public String getLpno() {
        return lpno;
    }

    public void setLpno(String lpno) {
        this.lpno = lpno;
    }

    public String getSeqno() {
        return seqno;
    }

    public void setSeqno(String seqno) {
        this.seqno = seqno;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getVhType() {
        return vhType;
    }

    public void setVhType(Integer vhType) {
        this.vhType = vhType;
    }

    public Integer getStatusT() {
        return statusT;
    }

    public void setStatusT(Integer statusT) {
        this.statusT = statusT;
    }

    public Long getEnrollDate() {
        return enrollDate;
    }

    public void setEnrollDate(Long enrollDate) {
        this.enrollDate = enrollDate;
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
    
    public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}
	
	public String getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}

	public String getFrameNumber() {
		return frameNumber;
	}

	public void setFrameNumber(String frameNumber) {
		this.frameNumber = frameNumber;
	}

	public String getEngineNumber() {
		return engineNumber;
	}

	public void setEngineNumber(String engineNumber) {
		this.engineNumber = engineNumber;
	}

	public String getEmissionStandard() {
		return emissionStandard;
	}

	public void setEmissionStandard(String emissionStandard) {
		this.emissionStandard = emissionStandard;
	}

	public Long getProductionDate() {
		return productionDate;
	}

	public void setProductionDate(Long productionDate) {
		this.productionDate = productionDate;
	}

	@Override
	public String toString() {
		return "Vehicle [id=" + id + ", lpno=" + lpno + ", seqno=" + seqno + ", departmentId=" + departmentId
				+ ", factory=" + factory + ", model=" + model + ", vhType=" + vhType + ", statusT=" + statusT
				+ ", enrollDate=" + enrollDate + ", vehicleType=" + vehicleType + ", frameNumber=" + frameNumber
				+ ", engineNumber=" + engineNumber + ", emissionStandard=" + emissionStandard + ", productionDate="
				+ productionDate + ", comments=" + comments + ", createUser=" + createUser + ", createTime="
				+ createTime + ", updateUser=" + updateUser + ", updateTime=" + updateTime + ", deleted=" + deleted
				+ "]";
	}

}
