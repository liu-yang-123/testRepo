package com.zcxd.gun.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 
 * @ClassName EmployeeVO
 * @Description 员工档案
 * @author 秦江南
 * @Date 2021年5月14日上午11:33:48
 */
@Data
public class EmployeeVO {

    /**
     * 唯一标识
     */
    private Long id;

    /**
     * 员工工号
     */
    @NotBlank(message = "员工工号不能为空")  
    @Size(max=32,message="empNo最大长度为32")
    private String empNo;

    /**
     * 员工姓名
     */
    @NotBlank(message = "员工姓名不能为空")  
    @Size(max=32,message="empName最大长度为32")
    private String empName;

    /**
     * 所属部门
     */
	@NotNull(message = "所属部门不能为空")
    private Long departmentId;

    /**
     * 岗位
     */
	@NotNull(message = "岗位不能为空")
    private Long jobIds;

    /**
     * 职务
     */
	@NotNull(message = "职务不能为空")
    private Integer title;

    /**
     * 性别 0-男 1-女
     */
	@NotNull(message = "性别不能为空")
    private Integer sex;

    /**
     * 身份证号
     */
    @NotBlank(message = "身份证号不能为空")
	@Size(max=32,message="idno最大长度为32")
    private String idno;

    /**
     * 民族
     */
    @NotBlank(message = "民族不能为空")
	@Size(max=32,message="nation最大长度为32")
    private String nation;

    /**
     * 婚姻: 0 未婚，1 - 已婚， 2 - 离异
     */
	@NotNull(message = "婚姻不能为空")
    private Integer marriage;

    /**
     * 学历
     */
    @NotBlank(message = "学历不能为空")
	@Size(max=32,message="education最大长度为32")
    private String education;

    /**
     * 毕业院校
     */
	@Size(max=32,message="school最大长度为32")
    private String school;

    /**
     * 政治面貌
     */
    @NotBlank(message = "政治面貌不能为空")
	@Size(max=32,message="politic最大长度为32")
    private String politic;

    /**
     * 兵役情况: 0 - 未服兵役，1 - 退役
     */
    private Integer military;

    /**
     * 户口所在地详细地址
     */
	@Size(max=64,message="idcardAddr最大长度为64")
    private String idcardAddr;

    /**
     * 户口所在地-区县
     */
	@Size(max=32,message="idcardDistrict最大长度为32")
    private String idcardDistrict;

    /**
     * 户口所在地-城市
     */
	@Size(max=32,message="idcardCity最大长度为32")
    private String idcardCity;

    /**
     * 户口所在地-省
     */
	@Size(max=32,message="idcardPrivince最大长度为32")
    private String idcardPrivince;

    /**
     * 居住地址
     */
	@Size(max=64,message="address最大长度为64")
    private String address;

    /**
     * 入职日期
     */
	@NotNull(message = "入职日期不能为空")
    private Long entryDate;


    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
	@Size(max=32,message="mobile最大长度为32")
    private String mobile;

    /**
     * 微信
     */
	@Size(max=32,message="wxId最大长度为32")
    private String wxId;

    /**
     * 照片
     */
	@Size(max=32,message="photoUrl最大长度为32")
    private String photoUrl;

    /**
     * 紧急联系人手机
     */
	@Size(max=32,message="contactMobile最大长度为32")
    private String contactMobile;

    /**
     * 紧急联系人姓名
     */
	@Size(max=32,message="contactName最大长度为32")
    private String contactName;

    /**
     * 紧急联系人关系
     */
	@Size(max=32,message="contactRelationship最大长度为32")
    private String contactRelationship;

    /**
     * 担保人姓名
     */
	@Size(max=32,message="guarantorName最大长度为32")
    private String guarantorName;

    /**
     * 担保人电话
     */
	@Size(max=32,message="guarantorMobile最大长度为32")
    private String guarantorMobile;
	
    /**
     * 服务证
     */
	@Size(max=32,message="serviceCertificate最大长度为32")
    private String serviceCertificate;

    /**
     * 编制
     */
	@NotNull(message = "编制不能为空")
	@Size(max=32,message="manningQuotas最大长度为32")
    private String manningQuotas;
    
    
    /**
     * 所属公司
     */
	@NotNull(message = "所属公司不能为空")
	@Size(max=32,message="affiliatedCompany最大长度为32")
    private String affiliatedCompany;

    /**
     * 合同到期时间
     */
    private Long expirationDate;
}
