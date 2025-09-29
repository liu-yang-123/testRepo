package com.zcxd.gun.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author zccc
 */
@Data
public class GunVO {
    /**
     * 唯一标识
     */
    private Long id;

    /**
     * 枪号/弹盒号
     */
    @NotBlank(message = "枪号/弹盒号不能为空")
    private String gunCode;

    /**
     * 持枪证号
     */
    @NotBlank(message = "持枪证号不能为空")
    private String gunLicenceNum;

    /**
     * 持枪证号Id
     */
    private Long gunLicenceId;

    /**
     * 配发日期
     */
    @NotBlank(message = "配发日期不能为空")
    private String buyDate;

    /**
     * 枪支类型
     */
    @NotBlank(message = "枪支类型不能为空")
    private Long gunCategory;

    /**
     * 所属
     */
    @NotBlank(message = "所属顶级部门不能为空")
    private Integer departmentId;

    /**
     * 备注
     */
    @Size(max=64,message="备注最大长度为64")
    private String remark;
}
