package com.zcxd.base.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName EmployeeAwardsVO
 * @Description 奖惩记录
 * @author 秦江南
 * @Date 2021年5月19日上午9:20:01
 */
@ApiModel("奖惩信息")
@Data
public class EmployeeAwardsVO {

    /**
     * 自增主键
     */
	@ApiModelProperty(value = "奖惩信息Id",required = false)
    private Long id;

    /**
     * 员工id
     */
	@ApiModelProperty(value = "员工ID",required = true)
	@NotNull(message = "员工ID不能为空")
    private Long empId;

    /**
     * 奖惩类型
     */
	@ApiModelProperty(value = "奖惩类型",required = true)
    @NotBlank(message = "奖惩类型不能为空")  
    @Size(max=32,message="奖惩类型最大长度为32")
    private String awardsType;

    /**
     * 奖惩时间
     */
	@ApiModelProperty(value = "奖惩时间",required = true)
	@NotNull(message = "奖惩时间不能为空")
    private Long awardsDate;

    /**
     * 备注
     */
	@ApiModelProperty(value = "备注",required = false)
	@Size(max=64,message="奖惩类型最大长度为64")
    private String comments;
    
}
