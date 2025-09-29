package com.zcxd.base.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 
 * @ClassName SchdCleanPassCodeVO
 * @Description 清机密码通行证管理
 * @author shijin
 * @Date 2021年7月5日下午3:41:10
 */
@ApiModel("清机密码通行证管理")
@Data
public class SchdCleanPassCodeVO {

    /**
     * 自增id
     */
	@ApiModelProperty(value = "计划id",required = false)
    private Long id;

	@ApiModelProperty(value = "员工Id",required = true)
	@NotNull(message = "员工Id不能为空")
    private Long empId;

    @ApiModelProperty(value = "银行Id",required = true)
    @NotNull(message = "银行Id不能为空")
    private Long bankId;

    @ApiModelProperty(value = "备案类型(0 - 密码通行备案，1 - 网点备案",required = true)
    @NotNull(message = "备案类型不能为空")
    private Integer passType;

    @ApiModelProperty(value = "密码通行证")
    private String passCode;

    @ApiModelProperty(value = "部门Id",required = true)
    @NotNull(message = "部门Id不能为空")
    private Long departmentId;
}
