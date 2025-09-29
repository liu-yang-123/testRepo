package com.zcxd.gun.vo;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author zccc
 */
@Data
public class GunCategoryVO {
    private Long id;

    /**
     * 枪支类型名称
     */
    @NotBlank(message = "类型名称不能为空")
    private String gunCategoryName;

    /**
     * 分类
     */
    @NotNull(message = "分类不能为空")
    @Min(value = 1)
    @Max(value = 2)
    private Integer gunType;

    /**
     * 部门
     */
    @NotNull(message = "部门不能为空")
    private Integer departmentId;

    /**
     * 备注
     */
    private String remark;
}
