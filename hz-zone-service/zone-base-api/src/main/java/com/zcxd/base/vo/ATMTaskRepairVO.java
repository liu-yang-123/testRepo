package com.zcxd.base.vo;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName ATMTaskRepairVO
 * @Description 维修任务
 * @author 秦江南
 * @Date 2021年6月22日下午3:47:58
 */
@ApiModel("维修任务")
@Data
public class ATMTaskRepairVO {
	/**
	 * ATM设备id
	 */
	@ApiModelProperty(value = "ATM设备id",required = true)
	@NotNull(message = "ATM设备id不能为空")
	private Long atmId;
	
    /**
     * 预约时间
     */
	@ApiModelProperty(value = "预约时间",required = false)
//	@Size(max=32,message="预约时间最大长度为32")
    private String planTime;

    /**
     * 故障部位
     */
	@ApiModelProperty(value = "故障部位",required = true)
	@NotBlank(message = "故障部位不能为空")
	@Size(max=128,message="故障部位最大长度为128")
    private String content;
    
    /**
     * 维修公司
     */
	@ApiModelProperty(value = "维修公司",required = false)
//	@Size(max=64,message="维修公司最大长度为64")
    private String repairCompany;
	
}
