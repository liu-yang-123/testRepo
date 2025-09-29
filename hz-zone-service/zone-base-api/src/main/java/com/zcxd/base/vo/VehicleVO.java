package com.zcxd.base.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName VehicleVO
 * @Description 车辆信息
 * @author 秦江南
 * @Date 2021年5月19日下午3:16:52
 */
@ApiModel("车辆信息")
@Data
public class VehicleVO {

    /**
     * 唯一标识
     */
	@ApiModelProperty(value = "车辆Id",required = false)
    private Long id;

    /**
     * 车牌号码
     */
    @ApiModelProperty(value = "车牌号码",required = true)
    @NotBlank(message = "车牌号码不能为空")
    @Size(max=32,message="车牌号码最大长度为32")
    private String lpno;

    /**
     * 车辆编号
     */
    @ApiModelProperty(value = "车辆编号",required = true)
    @NotBlank(message = "车辆编号不能为空")
    @Size(max=32,message="车辆编号最大长度为32")
    private String seqno;
    
    /**
     * 所属顶级部门
     */
	@ApiModelProperty(value = "所属顶级部门",required = true)
    @NotNull(message = "所属顶级部门不能为空")  
    private Long departmentId;

    /**
     * 制造商
     */
    @ApiModelProperty(value = "制造商",required = true)
    @NotBlank(message = "制造商不能为空")
    @Size(max=32,message="制造商最大长度为32")
    private String factory;

    /**
     * 型号
     */
    @ApiModelProperty(value = "型号",required = true)
    @NotBlank(message = "型号不能为空")
    @Size(max=32,message="型号最大长度为32")
    private String model;

    /**
     * 车辆种类
     */
    @ApiModelProperty(value = "车辆种类",required = true)
    @NotNull(message = "车辆种类不能为空")
    private Integer vhType;

    /**
     * 购买日期
     */
    @ApiModelProperty(value = "购买日期",required = false)
    private Long enrollDate;
    
    /**
     * 车辆类型
     */
    @ApiModelProperty(value = "车辆类型",required = false)
    private String vehicleType;
    
    /**
     * 车架号
     */
    @ApiModelProperty(value = "车架号",required = false)
    private String frameNumber;
    
    /**
     * 发动机号
     */
    @ApiModelProperty(value = "发动机号",required = false)
    private String engineNumber;
    
    /**
     * 排放标准
     */
    @ApiModelProperty(value = "排放标准",required = false)
    private String emissionStandard;
    
    /**
     * 出厂日期
     */
    @ApiModelProperty(value = "出厂日期",required = false)
    private Long productionDate;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注",required = false)
    @Size(max=32,message="备注最大长度为32")
    private String comments;

}
