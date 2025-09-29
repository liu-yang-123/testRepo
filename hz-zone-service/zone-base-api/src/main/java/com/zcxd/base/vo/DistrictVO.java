package com.zcxd.base.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 *
 * @ClassName DistrictVO
 * @Description 区域信息
 * @author liuyang
 * @Date 2025年9月21日下午2:41:29
 */
@ApiModel("区域信息")
@Data
public class DistrictVO {
    /**
     * 唯一标识
     */
    @ApiModelProperty(value = "Id",required = false)
    private Long id;

    /**
     * 区域名称
     */
    @ApiModelProperty(value = "区域名称",required = true)
    @NotBlank(message = "区域名称不能为空")
    @Size(max=64,message="区域名称最大长度为64")
    private String districtName;
}
