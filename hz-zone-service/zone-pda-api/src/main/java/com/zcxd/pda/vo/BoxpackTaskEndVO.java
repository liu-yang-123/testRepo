package com.zcxd.pda.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("早送晚收任务完成")
@Data
public class BoxpackTaskEndVO {
	
	@ApiModelProperty(value = "任务id", required = true)
	private Long taskId;
	
	@ApiModelProperty(value = "交接操作员Aid", required = true)
	private Long handOpManA;
	
	@ApiModelProperty(value = "交接操作员Bid", required = true)
	private Long handOpManB;
	
	@ApiModelProperty(value = "钥匙员id", required = true)
	private Long keyMan;
	
	@ApiModelProperty(value = "钥匙员姓名", required = true)
	private String keyManName;
	
	@ApiModelProperty(value = "密码员id", required = true)
	private Long operMan;
	
	@ApiModelProperty(value = "密码员姓名", required = true)
	private String operManName;
}
