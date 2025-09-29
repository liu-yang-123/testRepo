package com.zcxd.gun.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zccc
 */
@Data
public class GunSecurityQueryVO {
    /**
     * 员工Id
     */
    private Long employeeId;

    /**
     * 员工名称
     */
    private String name;

    /**
     * 保安证号
     */
    private String securityNum;

    /**
     * 保安证图片
     */
    private Boolean hasPhoto;

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
     * 部门Id
     */
    @NotNull
    private Integer departmentId;

}
