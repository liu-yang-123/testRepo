package com.zcxd.base.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName PDAVO
 * @Description 终端设备
 * @author 秦江南
 * @Date 2021年5月26日下午5:27:53
 */
@ApiModel("终端设备")
@Data
public class PDAVO {
	/**
     * 唯一标识
     */
	@ApiModelProperty(value = "终端设备Id",required = false)
	private Long id;
	
    /**
     * 终端编号
     */
	@ApiModelProperty(value = "终端编号",required = true)
    @NotBlank(message = "终端编号不能为空")  
    @Size(max=32,message="tersn最大长度为32")
    private String tersn;

    /**
     * 物理地址
     */
	@ApiModelProperty(value = "物理地址",required = false)
    @Size(max=32,message="mac最大长度为32")
    private String mac;

    /**
     * 用途 0: 公司 1: 银行
     */
	@ApiModelProperty(value = "用途",required = true)
    @NotNull(message = "用途不能为空")  
    private Integer useType;

    /**
     * 所属顶级部门
     */
	@ApiModelProperty(value = "所属顶级部门",required = true)
    @NotNull(message = "所属顶级部门不能为空")  
    private Long departmentId;
	
    /**
     * 所属机构
     */
	@ApiModelProperty(value = "所属机构",required = true)
    @NotNull(message = "所属机构不能为空")  
    private Long bankId;
	
    /**
     * 备注
     */
	@ApiModelProperty(value = "备注",required = false)
    @Size(max=32,message="comments最大长度为32") 
    private String comments;
	
}
