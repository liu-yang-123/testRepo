package com.zcxd.base.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName DepartmentVO
 * @Description 部门信息
 * @author 秦江南
 * @Date 2021年5月13日上午10:53:31
 */
@ApiModel("部门信息")
@Data
public class DepartmentVO {
	/**
     * 唯一标识
     */
	@ApiModelProperty(value = "用户Id",required = false)
    private Long id;

    /**
     * 部门名称
     */
    @ApiModelProperty(value = "部门名称",required = true)
    @NotBlank(message = "部门名称不能为空")
    @Size(max=32,message="部门名称最大长度为32")
    private String name;

    /**
     * 部门描述
     */
    @ApiModelProperty(value = "部门描述",required = false)
    @Size(max=32,message="部门描述最大长度为32")
    private String description;

    /**
     * 上级部门
     */
    @ApiModelProperty(value = "上级部门",required = true)
    private String parentIds;

    /**
     * 负责人姓名
     */
    @ApiModelProperty(value = "负责人姓名",required = false)
    @Size(max=32,message="负责人姓名最大长度为32")
    private String linkmanName;

    /**
     * 联系电话
     */
    @ApiModelProperty(value = "联系电话",required = false)
    @Size(max=32,message="联系电话最大长度为32")
    private String linkmanMobile;
}
