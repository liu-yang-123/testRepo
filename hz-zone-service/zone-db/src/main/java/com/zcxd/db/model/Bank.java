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
 * 银行机构网点
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public class Bank extends Model<Bank> {

    private static final long serialVersionUID=1L;

    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 部门（事业部id）
     */
    private Long departmentId;

    /**
     * 父级机构ID: /0/aaa/bbb
     */
    private String parentIds;

    /**
     * 银行编码
     */
    private String bankNo;
    /**
     * 机构名称
     */
    private String fullName;

    /**
     * 机构简称
     */
    private String shortName;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县
     */
    private String district;

    /**
     * 详细地址
     */
    private String address;
    
    /**
     * 营业时间及应急联系人
     */
    private String workInfo;
    
    /**
     * 线路编号
     */
    private String routeNo;
    
    /**
     * 回笼现金运送方式：0 - 钞袋， 1- 钞盒
     */
    private Integer carryType;

    /**
     * 位置经度
     */
    private BigDecimal locateLat;

    /**
     * 位置纬度
     */
    private BigDecimal locateLng;

    /**
     * 网点类型（清机/尾箱)
     */
    private Integer bankType;

    /**
     * 是否开通ATM加钞
     */
    private Integer haveAtm;
    /**
     * 是否开通尾箱业务
     */
    private Integer haveBox;
    /**
     * 是否开通商业清分
     */
    private Integer haveClear;
    /**
     * 是否开通金库业务
     */
    private Integer haveStore;

    /**
     * 机构状态
     */
    private Integer statusT;

    /**
     * 联系人
     */
    private String contact;

    /**
     * 联系人电话
     */
    private String contactPhone;

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
     * 逻辑删除
     */
    private Integer deleted;
    
    /**
     * 网点类型：1 - 总行，2 - 分行，3 - 支行，4 - 网点, 5 - 库房
     */
    private Integer bankLevel;

    /**
     * 机构种类：1 - 营业机构，2 - 非营业机构，3 - 库房
     */
    private Integer bankCategory;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getParentIds() {
        return parentIds;
    }

    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getLocateLat() {
        return locateLat;
    }

    public void setLocateLat(BigDecimal locateLat) {
        this.locateLat = locateLat;
    }

    public BigDecimal getLocateLng() {
        return locateLng;
    }

    public void setLocateLng(BigDecimal locateLng) {
        this.locateLng = locateLng;
    }

    public Integer getBankType() {
        return bankType;
    }

    public void setBankType(Integer bankType) {
        this.bankType = bankType;
    }

    public Integer getHaveAtm() {
        return haveAtm;
    }

    public void setHaveAtm(Integer haveAtm) {
        this.haveAtm = haveAtm;
    }

    public Integer getHaveBox() {
        return haveBox;
    }

    public void setHaveBox(Integer haveBox) {
        this.haveBox = haveBox;
    }

    public Integer getHaveClear() {
        return haveClear;
    }

    public void setHaveClear(Integer haveClear) {
        this.haveClear = haveClear;
    }

    public Integer getHaveStore() {
        return haveStore;
    }

    public void setHaveStore(Integer haveStore) {
        this.haveStore = haveStore;
    }

    public Integer getStatusT() {
        return statusT;
    }

    public void setStatusT(Integer statusT) {
        this.statusT = statusT;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
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

    public Integer getCarryType() {
		return carryType;
	}

	public void setCarryType(Integer carryType) {
		this.carryType = carryType;
	}
	
	public String getWorkInfo() {
		return workInfo;
	}

	public void setWorkInfo(String workInfo) {
		this.workInfo = workInfo;
	}

	public String getRouteNo() {
		return routeNo;
	}

	public void setRouteNo(String routeNo) {
		this.routeNo = routeNo;
	}
	
	public Integer getBankLevel() {
		return bankLevel;
	}

	public void setBankLevel(Integer bankLevel) {
		this.bankLevel = bankLevel;
	}

	public Integer getBankCategory() {
		return bankCategory;
	}

	public void setBankCategory(Integer bankCategory) {
		this.bankCategory = bankCategory;
	}

	@Override
	public String toString() {
		return "Bank [id=" + id + ", departmentId=" + departmentId + ", parentIds=" + parentIds + ", bankNo=" + bankNo
				+ ", fullName=" + fullName + ", shortName=" + shortName + ", province=" + province + ", city=" + city
				+ ", district=" + district + ", address=" + address + ", workInfo=" + workInfo + ", routeNo=" + routeNo
				+ ", carryType=" + carryType + ", locateLat=" + locateLat + ", locateLng=" + locateLng + ", bankType="
				+ bankType + ", statusT=" + statusT + ", contact=" + contact + ", contactPhone=" + contactPhone
				+ ", comments=" + comments + ", createUser=" + createUser + ", createTime=" + createTime
				+ ", updateUser=" + updateUser + ", updateTime=" + updateTime + ", deleted=" + deleted + ", bankLevel="
				+ bankLevel + ", bankCategory=" + bankCategory + "]";
	}

}
