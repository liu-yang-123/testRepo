package com.zcxd.gun.dto;

import lombok.Data;

/**
 * @author zccc
 */
@Data
public class GunSecurityDTO {
    private Long id;
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
     * 保安证图片
     */
    private String securityPhoto;

    /**
     * 保安证派发机构
     */
    private String authority;

    /**
     * 保安证状态
     * 1：在库
     */
    private Integer status;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 身份证号
     */
    private String idno;

    /**
     * 在职状态：0 - 在职，1 - 离职
     */
    private Integer statusT;

}
