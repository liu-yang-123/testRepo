package com.zcxd.base.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName RouteTemplateBankVO
 * @Description 线路模板网点
 * @author 秦江南
 * @Date 2021年11月24日下午5:47:09
 */
@ApiModel("线路模板网点")
@Data
public class RouteTemplateBankVO {

	
    /**
     * 线路模板id
     */
	@ApiModelProperty(value = "线路模板id",required = true)
	@NotNull(message = "线路模板id不能为空")
    private Long routeTemplateId;

    /**
     * 途径网点
     */
	@ApiModelProperty(value = "途径网点id",required = true)
	@NotNull(message = "途径网点id不能为空")
    private Long bankId;

}
