package com.zcxd.base.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName RouteTemplateATMVO
 * @Description 线路模板设备
 * @author 秦江南
 * @Date 2021年6月18日上午11:15:49
 */
@ApiModel("线路模板设备")
@Data
public class RouteTemplateATMVO {

	/**
     * 唯一标识
     */
	@ApiModelProperty(value = "线路模板设备Id",required = false)
    private Long id;

	
    /**
     * 线路模板id
     */
	@ApiModelProperty(value = "线路模板id",required = true)
	@NotNull(message = "线路模板id不能为空")
    private Long routeTemplateId;

    /**
     * 设备id
     */
	@ApiModelProperty(value = "设备id",required = true)
	@NotBlank(message = "设备id不能为空")  
    private String atmId;

//    /**
//     * 所属网点
//     */
//	@ApiModelProperty(value = "所属网点id",required = true)
//	@NotNull(message = "所属网点id不能为空")
//    private Long bankId;

}
