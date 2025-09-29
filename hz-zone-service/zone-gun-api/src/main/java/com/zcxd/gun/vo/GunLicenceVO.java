package com.zcxd.gun.vo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author zccc
 */
@Data
public class GunLicenceVO {
    private Long id;
    /**
     * 员工Id
     */
    @NotBlank(message = "员工Id不能为空")
    private Long employeeId;

    /**
     * 持枪证号
     */
    @NotBlank(message = "持枪证号不能为空")
    private String gunLicenceNum;

    /**
     * 持枪证有效期
     */
    @NotBlank(message = "持枪证有效期不能为空")
    @Min(value = 1, message = "持枪证有效期填写有误")
    private Long gunLicenceValidity;

    /**
     * 持枪证图片地址
     */
    private String gunLicencePhoto;

    /**
     * 证件管理状态
     */
    private String licenceStatus;

    /**
     * 备注
     */
    @Size(max = 128,message = "备注最大长度为128")
    private String remark;

    @NotBlank(message = "部门ID不能为空")
    private Integer departmentId;

    public GunLicenceVO(){}

    public GunLicenceVO(String gunLicenceNum, Integer departmentId) {
        this.gunLicenceNum = gunLicenceNum;
        this.departmentId = departmentId;
    }
}
