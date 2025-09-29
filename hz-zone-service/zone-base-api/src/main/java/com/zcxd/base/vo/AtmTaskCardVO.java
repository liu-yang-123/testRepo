package com.zcxd.base.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 
 * @ClassName AtmTaskCardVO
 * @Description 吞没卡信息
 * @author 秦江南
 * @Date 2021年9月13日下午1:11:26
 */
@ApiModel("吞没卡信息")
@Data
public class AtmTaskCardVO {

	@ApiModelProperty(value = "线路id",required = true)
	@NotNull(message = "线路id不能为空")
    private Long routeId;
	
	@ApiModelProperty(value = "线路编号",required = true)
	@NotBlank(message = "线路编号不能为空")
    private String routeNo;
    
	@ApiModelProperty(value = "任务id",required = true)
	@NotNull(message = "任务id不能为空")
    private Long taskId;
	
	@ApiModelProperty(value = "类型",required = true)
	@NotNull(message = "类型不能为空")
	private Integer category;

	@ApiModelProperty(value = "发卡行",required = true)
	@NotBlank(message = "发卡行不能为空")
	@Size(max=64,message="发卡行最大长度为32")
    private String cardBank;
	
	@ApiModelProperty(value = "银行卡号",required = true)
	@NotBlank(message = "银行卡号不能为空")
	@Size(max=64,message="银行卡号最大长度为32")
    private String cardNo;

}
