package com.zcxd.base.vo;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName MenuVO
 * @Description 菜单信息
 * @author 秦江南
 * @Date 2021年5月10日下午7:02:30
 */
@ApiModel("菜单信息")
@Data
public class MenuVO implements Serializable {

    /**
     * 菜单ID
     */
	@ApiModelProperty(value = "菜单Id",required = false)
    private Long id;

    /**
     * 菜单名称
     */
    @ApiModelProperty(value = "菜单名称",required = true)
    @NotBlank(message = "菜单名称不能为空")
    @Size(max=32,message="name最大长度为32")
    private String name;

    /**
     * 父级菜单
     */
    @ApiModelProperty(value = "父级菜单Id",required = false)
    private Long pid;

    /**
     * 地址
     */
    @ApiModelProperty(value = "菜单地址",required = false)
    @Size(max=64,message="url最大长度为64")
    private String url;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序",required = true)
    @NotNull(message="sort不能为空")
    private Integer sort;

}
