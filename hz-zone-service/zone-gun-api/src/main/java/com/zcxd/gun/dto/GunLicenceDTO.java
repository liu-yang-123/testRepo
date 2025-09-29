package com.zcxd.gun.dto;

import lombok.Data;

/**
 * @author zccc
 */
@Data
public class GunLicenceDTO {
    /**
     * 员工Id
     */
    private Long employeeId;

    /**
     * 员工姓名
     */
    private String empName;

    /**
     * 员工工号
     */
    private String empNo;

    /**
     * 保安证号
     */
    private String securityNum;

    /**
     * 持枪证
     */
    private String gunLicenceNum;

    /**
     * 持枪证图片
     */
    private String gunLicencePhoto;

    /**
     * 持枪证有效期
     */
    private Long gunLicenceValidity;

    /**
     * 证件管理状态
     */
    private String licenceStatus;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 身份证号
     */
    private String idno;

    /**
     * 岗位名称
     */
    private String jobName;

    /**
     * 在职状态：0 - 在职，1 - 离职
     */
    private Integer statusT;
}
