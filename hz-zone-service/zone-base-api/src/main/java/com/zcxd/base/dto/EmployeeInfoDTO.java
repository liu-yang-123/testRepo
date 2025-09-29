package com.zcxd.base.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName EmployeeInfoDTO
 * @Description 员工档案详细信息
 * @author 秦江南
 * @Date 2021年5月18日上午10:48:37
 */
@Data
public class EmployeeInfoDTO {


    /**
     * 员工工号
     */
    private String empNo;

    /**
     * 员工姓名
     */
    private String empName;

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
     * 合同到期时间
     */
    private Long expirationDate;

    /**
     * 备注
     */
    private String comments;

    /**
     * 下次家访时间
     */
    private Long nextVisitDate;
	
}
