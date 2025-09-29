package com.zcxd.base.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName EmployeeJobVO
 * @Description 岗位信息
 * @author 秦江南
 * @Date 2021年5月13日下午4:40:34
 */
@ApiModel("岗位信息")
@Data
public class EmployeeJobVO {


    /**
     * 唯一标识
     */
	@ApiModelProperty(value = "岗位Id",required = false)
    private Long id;

    /**
     * 岗位名称
     */
	@ApiModelProperty(value = "岗位名",required = true)
    @NotBlank(message = "岗位名不能为空")  
    @Size(max=32,message="name最大长度为32")
    private String name;
	
    /**
     * 岗位类型
     */
	@ApiModelProperty(value = "岗位类型",required = true)
    @NotNull(message = "岗位类型不能为空")  
    private Integer jobType;

    /**
     * 岗位描述
     */
	@ApiModelProperty(value = "岗位描述",required = false)
    @Size(max=32,message="descript最大长度为32") 
    private String descript;

}
