package com.zcxd.base.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName UserVO
 * @Description 用户VO
 * @author 秦江南
 * @Date 2021年5月12日上午11:46:10
 */
@ApiModel("用户信息")
@Data
public class UserVO {
	/**
     * 唯一标识
     */
	@ApiModelProperty(value = "用户Id",required = false)
    private Long id;

    /**
     * 所属部门
     */
	@ApiModelProperty(value = "部门Id",required = false)
    private Long departmentId;
	
    /**
     * 数据权限部门
     */
	@ApiModelProperty(value = "数据权限部门",required = true)
	@NotBlank(message = "数据权限部门不能为空")
    private String authDepartments;

    /**
     * 用户账号
     */
    @ApiModelProperty(value = "用户名称",required = true)
    @NotBlank(message = "用户名称不能为空")
    @Size(max=32,message="username最大长度为32")
    private String username;


    /**
     * 用户姓名
     */
    @ApiModelProperty(value = "真实姓名",required = true)
    @NotBlank(message = "真实姓名不能为空")
    @Size(max=32,message="nickName最大长度为32")
    private String nickName;

    /**
     * 角色列表
     */
    @ApiModelProperty(value = "角色列表",required = true)
    @NotBlank(message = "角色列表不能为空")
    private String roleIds;
}
