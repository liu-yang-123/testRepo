package com.zcxd.base.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName RoleVO
 * @Description 角色信息
 * @author 秦江南
 * @Date 2021年5月13日下午5:01:40
 */
@ApiModel("角色信息")
@Data
public class RoleVO {
	/**
     * 唯一标识
     */
	@ApiModelProperty(value = "角色Id",required = false)
    private Long id;

    /**
     * 角色名
     */
	@ApiModelProperty(value = "角色名",required = true)
    @NotBlank(message = "角色名不能为空")  
    @Size(max=32,message="roleName最大长度为32")
    private String roleName;

    /**
     * 角色描述
     */
	@ApiModelProperty(value = "角色描述",required = false)
    @Size(max=32,message="describes最大长度为32") 
    private String describes;
}
