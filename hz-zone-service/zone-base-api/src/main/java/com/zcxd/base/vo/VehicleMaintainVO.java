package com.zcxd.base.vo;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName VehicleMaintainVO
 * @Description 车辆维保信息
 * @author 秦江南
 * @Date 2021年5月19日下午4:13:07
 */
@ApiModel("车辆维保信息")
@Data
public class VehicleMaintainVO {


    /**
     * 唯一标识
     */
	@ApiModelProperty(value = "维保记录Id",required = false)
    private Long id;

    /**
     * 设备编号
     */
	@ApiModelProperty(value = "设备编号",required = true)
	@Min(value=1, message="设备编号不能小于1")
    private Long vehicleId;
	
    /**
     * 所属顶级部门
     */
	@ApiModelProperty(value = "所属顶级部门",required = true)
    @NotNull(message = "所属顶级部门不能为空")  
    private Long departmentId;

    /**
     * 维保日期
     */
	@ApiModelProperty(value = "维保日期",required = true)
    private Long mtDate;

    /**
     * 维保类型
     */
    @ApiModelProperty(value = "维保类型",required = true)
    @NotBlank(message = "维保类型不能为空")
    @Size(max=32,message="维保类型最大长度为32")
    private String mtType;

    /**
     * 故障原因
     */
    @ApiModelProperty(value = "故障原因",required = false)
    @Size(max=32,message="故障原因最大长度为32")
    private String mtReason;

    /**
     * 维保内容
     */
    @ApiModelProperty(value = "维保内容",required = false)
    @Size(max=64,message="维保内容最大长度为64")
    private String mtContent;

    /**
     * 维保成本
     */
    @ApiModelProperty(value = "维保成本",required = false)
    private Double mtCost;

    /**
     * 维保结果: 1 - 维修成功，0- 维修失败
     */
    @ApiModelProperty(value = "维保结果",required = true)
    @NotNull(message = "维保结果不能为空")
    private Integer mtResult;

    /**
     * 经办人
     */
    @ApiModelProperty(value = "经办人",required = true)
    @Min(value=1, message="经办人Id不能小于1")
    private Integer mtEmployee;

}
