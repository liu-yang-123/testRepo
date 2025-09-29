package com.zcxd.base.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 
 * @ClassName SchdVacateSettingDetailDTO
 */
@ApiModel("休息计划设置")
@Data
public class SchdVacatePlanDTO {


    private Long id;

    private String name;

    private Long beginDate;

    private Long endDate;

    private Long createTime;

    private Long updateTime;

    private String createUserName;

    private String updateUserName;
}
