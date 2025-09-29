package com.zcxd.pda.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 * 线路任务导入DTO
 */
@Data
public class ImportAtmTaskItem {

    @ApiModelProperty(value = "设备编号",required = true)
    @NotBlank(message = "设备编号不能为空")
    @Size(max=16,min = 6,message="设备编号最大长度为16")
    private String atmNo;//atm设备编号

    @ApiModelProperty(value = "加钞数量",required = true)
    @NotNull(message = "加钞数量不能为空")
    private Float addBundles; //加钞金额

    @ApiModelProperty(value = "加钞备用款")
    @NotNull(message = "加钞备用款不能为空")
    private Float bakBundles; //备用金额

    @ApiModelProperty(value = "任务特殊说明")
    private String comments; //加钞特殊说明

}
