package com.zcxd.base.vo;

import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName EmployeeQueryVO
 * @Description 查询员工档案
 * @author 秦江南
 * @Date 2021年5月17日下午3:28:09
 */
@ApiModel("查询员工档案")
@Data
public class EmployeeQueryVO {

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
	 * 低年龄
	 */
	@ApiModelProperty(value = "低年龄",required = false)
	private Integer minAge;
	
	/**
	 * 高年龄
	 */
	@ApiModelProperty(value = "高年龄",required = false)
	private Integer maxAge;
	
	/**
	 * 政治面貌
	 */
	@ApiModelProperty(value = "政治面貌",required = false)
    @Size(max=32,message="政治面貌最大长度为32") 
	private String politic;
	
	/**
	 * 教育程度
	 */
	@ApiModelProperty(value = "教育程度",required = false)
    @Size(max=32,message="教育程度最大长度为32") 
	private String education;
	
	/**
	 * 性别
	 */
	@ApiModelProperty(value = "性别",required = false)
	private Integer sex;
	
	/**
	 * 岗位
	 */
	@ApiModelProperty(value = "岗位",required = false)
	private Long jobId;
	
	/**
	 * 部门
	 */
	@ApiModelProperty(value = "部门",required = false)
	private Long departmentId;
	
	/**
	 * 离职状态
	 */
	@ApiModelProperty(value = "离职状态",required = false)
	private Integer status;

}
