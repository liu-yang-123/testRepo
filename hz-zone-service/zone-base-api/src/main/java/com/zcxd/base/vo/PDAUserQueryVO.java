package com.zcxd.base.vo;

import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName PDAUserQueryVO
 * @Description PDA用户列表查询条件
 * @author 秦江南
 * @Date 2021年5月25日下午7:20:59
 */
@ApiModel("PDA用户列表查询条件")
@Data
public class PDAUserQueryVO {

	/**
	 * 工号
	 */
	@ApiModelProperty(value = "工号",required = false)
    @Size(max=32,message="工号最大长度为32") 
	private String empNo;
	
	/**
	 * 姓名
	 */
	@ApiModelProperty(value = "姓名",required = false)
    @Size(max=32,message="姓名最大长度为32") 
	private String empName;
	
	/**
	 * 部门
	 */
	@ApiModelProperty(value = "部门",required = false)
	private Long departmentId;
	
	/**
	 * 岗位
	 */
	@ApiModelProperty(value = "岗位",required = false)
	private Long jobId;
	
	/**
	 * 是否启用PDA
	 */
	@ApiModelProperty(value = "是否启用PDA",required = false)
	private Integer pdaEnable;
}
