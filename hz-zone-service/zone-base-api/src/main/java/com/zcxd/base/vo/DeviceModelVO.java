package com.zcxd.base.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author songanwei
 * @date 2021-05-14
 */
@ApiModel("设备型号")
@Data
public class DeviceModelVO {

    /**
     * 型号ID
     */
    @ApiModelProperty("型号ID")
    private Long id;

    /**
     * 品牌ID
     */
    @NotNull
    @ApiModelProperty("品牌ID")
    private Long factoryId;

    /**
     * 设备型号
     */
    @NotNull
    @ApiModelProperty("设备型号")
    private String modelName;

    /**
     * 设备分类
     */
    @NotNull
    @ApiModelProperty("设备分类")
    private String deviceType;

    /**
     * 规格
     */
    @Size(max = 64,message = "规格长度不能超过64个字符")
    @NotNull
    @ApiModelProperty("规格")
    private String size;

    /**
     * 产能
     */
    @Min(value = 0,message = "产能必须大于等于0")
    @ApiModelProperty("产能")
    private Integer speed;
}
