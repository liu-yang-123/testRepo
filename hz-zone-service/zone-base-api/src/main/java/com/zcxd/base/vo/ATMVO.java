package com.zcxd.base.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName ATMVO
 * @Description ATM设备
 * @author 秦江南
 * @Date 2021年5月27日下午3:58:40
 */
@ApiModel("ATM设备")
@Data
public class ATMVO {

    /**
     * 自增
     */
	@ApiModelProperty(value = "ATM设备Id",required = false)
    private Long id;

    /**
     * 终端编号
     */
    @ApiModelProperty(value = "终端编号",required = true)
    @NotBlank(message = "终端编号不能为空")
    @Size(max=64,message="终端编号最大长度为64")
    private String terNo;

    /**
     * 设备类型
     */
    @ApiModelProperty(value = "设备类型",required = true)
    @NotBlank(message = "设备类型不能为空")
    @Size(max=64,message="设备类型最大长度为64")
    private String terType;

    /**
     * 设备品牌
     */
    @ApiModelProperty(value = "设备品牌",required = true)
    @NotBlank(message = "设备品牌不能为空")
    @Size(max=64,message="设备品牌最大长度为64")
    private String terFactory;

    /**
     * 加钞券别
     */
	@ApiModelProperty(value = "加钞券别",required = true)
    @NotNull(message = "加钞券别不能为空")  
    private Integer denom;
    
    /**
     * 位置类型（离行式..)
     */
    @ApiModelProperty(value = "位置类型",required = true)
    @NotNull(message = "位置类型不能为空")
    private Integer locationType;

    /**
     * 所属机构
     */
	@ApiModelProperty(value = "所属机构",required = true)
    @NotNull(message = "所属机构不能为空")  
    private Long bankId;
	
	/**
     * 装机地址
     */
	@ApiModelProperty(value = "装机地址",required = false)
    private String installInfo;
    
    /**
     * 取吞卡网点
     */
	@ApiModelProperty(value = "取吞卡网点",required = true)
    @NotNull(message = "取吞卡网点不能为空")  
    private Long gulpBankId;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注",required = false)
    @Size(max=64,message="备注最大长度为64")
    private String comments;

}
