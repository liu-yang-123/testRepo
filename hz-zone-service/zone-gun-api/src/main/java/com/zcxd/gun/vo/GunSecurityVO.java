package com.zcxd.gun.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author zccc
 */
@Data
public class GunSecurityVO {
    private Long id;
    /**
     * 员工Id
     */
    @NotBlank(message = "员工Id不能为空")
    private Long employeeId;

    /**
     * 保安证号
     */
    @NotBlank(message = "保安证号不能为空")
    private String securityNum;

    /**
     * 保安证图片地址
     */
    private String securityPhoto;

    /**
     * 保安证派发机构
     */
    @NotBlank(message = "保安证派发机构不能为空")
    private String authority;

    /**
     * 备注
     */
    @Size(max = 128,message = "备注最大长度为128")
    private String remark;

    @NotBlank(message = "部门ID不能为空")
    private Integer departmentId;
}
