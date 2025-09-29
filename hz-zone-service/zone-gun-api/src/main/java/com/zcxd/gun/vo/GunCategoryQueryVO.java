package com.zcxd.gun.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zccc
 */
@Data
public class GunCategoryQueryVO {

    /**
     * 枪支类型名称
     */
    private String gunCategoryName;

    /**
     * 分类
     * 1:枪支，2：弹盒
     */
    private Integer gunType;

    /**
     * 部门
     */
    @NotBlank(message = "部门不能为空")
    private String departmentId;

}
