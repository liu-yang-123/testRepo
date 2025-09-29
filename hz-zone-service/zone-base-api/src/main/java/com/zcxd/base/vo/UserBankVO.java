package com.zcxd.base.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName UserBankVO
 * @Description 银行用户信息
 * @author 秦江南
 * @Date 2021年10月9日下午2:02:04
 */
@ApiModel("银行用户信息")
@Data
public class UserBankVO extends UserVO{
	/**
     * 用户所属银行
     */
	@ApiModelProperty(value = "银行Id",required = false)
    private Long bankId;
	
	/**
	 * 库存网点
	 */
	@ApiModelProperty(value = "库存网点",required = false)
	private String stockBank;
}
