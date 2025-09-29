package com.zcxd.base.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;

/**
 * @author songanwei
 * @date 2021-05-14
 */
@ApiModel("设备品牌")
@Data
public class DeviceFactoryVO {

    /**
     * 唯一标识
     */
    @ApiModelProperty(value = "品牌Id",required = false)
    private Long id;

    /**
     * 设备名称
     */
    @Size(max = 128,message = "品牌名称长度不超过128字符")
    @ApiModelProperty(value = "品牌名称",required = true)
    private String name;

    /**
     * 品牌简称
     */
    @Size(max = 128,message = "品牌简称长度不超过128字符")
    @ApiModelProperty(value = "品牌简称")
    private String shortName;

    /**
     * 地址
     */
    @Size(max = 255,message = "地址长度不超过255字符")
    @ApiModelProperty("地址")
    private String address;

    /**
     * 联系人
     */
    @Size(max = 32,message = "联系人长度不超过32字符")
    @ApiModelProperty("联系人")
    private String contact;

    /**
     * 联系电话
     */
    @Size(max = 16,message = "联系电话长度不超过16字符")
    @ApiModelProperty("联系电话")
    private String contactPhone;
}
