package com.zcxd.base.vo;

import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName RouteHandoverVO
 * @Description 交接信息
 * @author 秦江南
 * @Date 2022年11月21日上午10:15:34
 */
@ApiModel("交接信息")
@Data
public class RouteHandoverVO {

    /**
     * 唯一标识
     */
	@ApiModelProperty(value = "线路Id",required = true)
    private Long id;

    /**
     * 钞盒数
     */
	@ApiModelProperty(value = "钞盒数",required = true)
    private Integer boxCount;

    /**
     * 钞袋数
     */
	@ApiModelProperty(value = "钞袋数",required = true)
    private Integer bagCount;


    /**
     * 备注
     */
	@ApiModelProperty(value = "备注",required = false)
    @Size(max=64,message="comments最大长度为64")
    private String comments;

}
