package com.zcxd.db.model;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

/**
 * <p>
 * 员工信息
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public class Employee extends Model<Employee> {

    private static final long serialVersionUID=1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 员工工号
     */
    private String empNo;

    /**
     * 员工姓名
     */
    private String empName;
    
    /**
     * 服务证
     */
    private String serviceCertificate;

    /**
     * 编制
     */
    private String manningQuotas;
    
    
    /**
     * 所属公司
     */
    private String affiliatedCompany;
    
    /**
     * 所属部门
     */
    private Long departmentId;

    /**
     * 岗位
     */
    private Long jobIds;
    
    /**
     * 岗位类型
     */
    private Integer jobType;

    /**
     * 职务
     */
    private Integer title;

    /**
     * 性别 0-男 1-女
     */
    private Integer sex;

    /**
     * 身份证号
     */
    private String idno;

    /**
     * 民族
     */
    private String nation;

    /**
     * 出生日期: yyyy-MM-dd 时间戳
     */
    private Long birthday;

    /**
     * 婚姻: 0 未婚，1 - 已婚， 2 - 离异
     */
    private Integer marriage;

    /**
     * 学历
     */
    private String education;

    /**
     * 毕业院校
     */
    private String school;

    /**
     * 政治面貌
     */
    private String politic;

    /**
     * 兵役情况: 0 - 未服兵役，1 - 退役
     */
    private Integer military;

    /**
     * 户口所在地详细地址
     */
    private String idcardAddr;

    /**
     * 户口所在地-区县
     */
    private String idcardDistrict;

    /**
     * 户口所在地-城市
     */
    private String idcardCity;

    /**
     * 户口所在地-省
     */
    private String idcardPrivince;

    /**
     * 居住地址
     */
    private String address;

    /**
     * 在职状态：0 - 在职，1 - 离职
     */
    private Integer statusT;

    /**
     * 入职日期
     */
    private Long entryDate;
    
    /**
     * 合同到期时间
     */
    private Long expirationDate;

    
    /**
     * 离职日期
     */
    private Long quitDate;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 微信
     */
    private String wxId;

    /**
     * 微信openid
     */
    private String wxOpenid;

    /**
     * 照片
     */
    private String photoUrl;

    /**
     * 紧急联系人手机
     */
    private String contactMobile;
    /**
     * 紧急联系人姓名
     */
    private String contactName;
    /**
     * 紧急联系人关系
     */
    private String contactRelationship;
    /**
     * 担保人姓名
     */
    private String guarantorName;
    /**
     * 担保人电话
     */
    private String guarantorMobile;
    /**
     * 备注
     */
    private String comments;

    /**
     * 下次家访时间
     */
    private Long nextVisitDate;
    
    private Integer pdaEnable;
    
    private String password;

    //是否PDA系统管理员
    private Integer pdaAdmin;
    
    private Long roleId;

    private Integer routeLeader;

    /**
     * 创建人
     */
//    @TableField(fill= FieldFill.INSERT)
    private Long createUser;

    /**
     * 创建时间
     */
    @TableField(fill= FieldFill.INSERT)
    private Long createTime;

    /**
     * 更新人
     */
//    @TableField(fill= FieldFill.INSERT_UPDATE)
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


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmpNo() {
        return empNo;
    }

    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getJobIds() {
        return jobIds;
    }

    public void setJobIds(Long jobIds) {
        this.jobIds = jobIds;
    }

    public Integer getTitle() {
        return title;
    }

    public void setTitle(Integer title) {
        this.title = title;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getIdno() {
        return idno;
    }

    public void setIdno(String idno) {
        this.idno = idno;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public Long getBirthday() {
        return birthday;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
    }

    public Integer getMarriage() {
        return marriage;
    }

    public void setMarriage(Integer marriage) {
        this.marriage = marriage;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getPolitic() {
        return politic;
    }

    public void setPolitic(String politic) {
        this.politic = politic;
    }

    public Integer getMilitary() {
        return military;
    }

    public void setMilitary(Integer military) {
        this.military = military;
    }

    public String getIdcardAddr() {
        return idcardAddr;
    }

    public void setIdcardAddr(String idcardAddr) {
        this.idcardAddr = idcardAddr;
    }

    public String getIdcardDistrict() {
        return idcardDistrict;
    }

    public void setIdcardDistrict(String idcardDistrict) {
        this.idcardDistrict = idcardDistrict;
    }

    public String getIdcardCity() {
        return idcardCity;
    }

    public void setIdcardCity(String idcardCity) {
        this.idcardCity = idcardCity;
    }

    public String getIdcardPrivince() {
        return idcardPrivince;
    }

    public void setIdcardPrivince(String idcardPrivince) {
        this.idcardPrivince = idcardPrivince;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getStatusT() {
        return statusT;
    }

    public void setStatusT(Integer statusT) {
        this.statusT = statusT;
    }

    public Long getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Long entryDate) {
        this.entryDate = entryDate;
    }

    public Long getQuitDate() {
        return quitDate;
    }

    public void setQuitDate(Long quitDate) {
        this.quitDate = quitDate;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getWxId() {
        return wxId;
    }

    public void setWxId(String wxId) {
        this.wxId = wxId;
    }

    public String getWxOpenid() {
        return wxOpenid;
    }

    public void setWxOpenid(String wxOpenid) {
        this.wxOpenid = wxOpenid;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getContactMobile() {
        return contactMobile;
    }

    public void setContactMobile(String contactMobile) {
        this.contactMobile = contactMobile;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactRelationship() {
        return contactRelationship;
    }

    public void setContactRelationship(String contactRelationship) {
        this.contactRelationship = contactRelationship;
    }

    public String getGuarantorName() {
        return guarantorName;
    }

    public void setGuarantorName(String guarantorName) {
        this.guarantorName = guarantorName;
    }

    public String getGuarantorMobile() {
        return guarantorMobile;
    }

    public void setGuarantorMobile(String guarantorMobile) {
        this.guarantorMobile = guarantorMobile;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Long getNextVisitDate() {
        return nextVisitDate;
    }

    public void setNextVisitDate(Long nextVisitDate) {
        this.nextVisitDate = nextVisitDate;
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

    
    
    public Integer getPdaEnable() {
		return pdaEnable;
	}

	public void setPdaEnable(Integer pdaEnable) {
		this.pdaEnable = pdaEnable;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

    public Integer getPdaAdmin() {
        return pdaAdmin;
    }

    public void setPdaAdmin(Integer pdaAdmin) {
        this.pdaAdmin = pdaAdmin;
    }

    public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

    public Integer getRouteLeader() {
        return routeLeader;
    }

    public void setRouteLeader(Integer routeLeader) {
        this.routeLeader = routeLeader;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
	
	public Integer getJobType() {
		return jobType;
	}

	public void setJobType(Integer jobType) {
		this.jobType = jobType;
	}
	
	public String getManningQuotas() {
		return manningQuotas;
	}

	public void setManningQuotas(String manningQuotas) {
		this.manningQuotas = manningQuotas;
	}

	public String getAffiliatedCompany() {
		return affiliatedCompany;
	}

	public void setAffiliatedCompany(String affiliatedCompany) {
		this.affiliatedCompany = affiliatedCompany;
	}

	public Long getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Long expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getServiceCertificate() {
		return serviceCertificate;
	}

	public void setServiceCertificate(String serviceCertificate) {
		this.serviceCertificate = serviceCertificate;
	}

	@Override
	public String toString() {
		return "Employee [id=" + id + ", empNo=" + empNo + ", empName=" + empName + ", serviceCertificate="
				+ serviceCertificate + ", manningQuotas=" + manningQuotas + ", affiliatedCompany=" + affiliatedCompany
				+ ", departmentId=" + departmentId + ", jobIds=" + jobIds + ", jobType=" + jobType + ", title=" + title
				+ ", sex=" + sex + ", idno=" + idno + ", nation=" + nation + ", birthday=" + birthday + ", marriage="
				+ marriage + ", education=" + education + ", school=" + school + ", politic=" + politic + ", military="
				+ military + ", idcardAddr=" + idcardAddr + ", idcardDistrict=" + idcardDistrict + ", idcardCity="
				+ idcardCity + ", idcardPrivince=" + idcardPrivince + ", address=" + address + ", statusT=" + statusT
				+ ", entryDate=" + entryDate + ", expirationDate=" + expirationDate + ", quitDate=" + quitDate
				+ ", mobile=" + mobile + ", wxId=" + wxId + ", wxOpenid=" + wxOpenid + ", photoUrl=" + photoUrl
				+ ", contactMobile=" + contactMobile + ", contactName=" + contactName + ", contactRelationship="
				+ contactRelationship + ", guarantorName=" + guarantorName + ", guarantorMobile=" + guarantorMobile
				+ ", comments=" + comments + ", nextVisitDate=" + nextVisitDate + ", pdaEnable=" + pdaEnable
				+ ", password=" + password + ", roleId=" + roleId + ", routeLeader=" + routeLeader + ", createUser="
				+ createUser + ", createTime=" + createTime + ", updateUser=" + updateUser + ", updateTime="
				+ updateTime + ", deleted=" + deleted + "]";
	}

}
