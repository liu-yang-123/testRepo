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
 * @ClassName RouteVO
 * @Description 线路管理
 * @author 秦江南
 * @Date 2021年5月28日下午3:30:23
 */
@ApiModel("线路管理")
@Data
public class RouteVO {

    /**
     * 唯一标识
     */
	@ApiModelProperty(value = "线路Id",required = false)
    private Long id;
	
    /**
     * 所属顶级部门
     */
	@ApiModelProperty(value = "所属顶级部门",required = true)
    @NotNull(message = "所属顶级部门不能为空")  
    private Long departmentId;

    /**
     * 线路编号
     */
	@ApiModelProperty(value = "线路编号",required = true)
    @NotBlank(message = "线路编号不能为空")  
    @Size(max=32,message="routeNo最大长度为32")
    private String routeNo;

    /**
     * 线路名称
     */
	@ApiModelProperty(value = "线路名称",required = true)
    @NotBlank(message = "线路名称不能为空")  
    @Size(max=32,message="routeName最大长度为32")
    private String routeName;

    /**
     * 分配车辆
     */
	@ApiModelProperty(value = "分配车辆",required = true)
    @NotNull(message = "分配车辆不能为空")  
	@Min(value=1, message="vehicleId不能小于1")
    private Integer vehicleId;

    /**
     * 任务日期
     */
	@ApiModelProperty(value = "任务日期",required = true)
    @NotNull(message = "任务日期不能为空")  
    private Long routeDate;

//    /**
//     * 计划开始时间
//     */
//	@ApiModelProperty(value = "计划开始时间",required = true)
//    @NotNull(message = "计划开始时间不能为空")  
//    private String planBeginTime;
//
//    /**
//     * 计划结束时间
//     */
//	@ApiModelProperty(value = "计划结束时间",required = true)
//    @NotNull(message = "计划结束时间不能为空")  
//    private String planFinishTime;
	
	/**
	 * 模板类型
	 */
	@ApiModelProperty(value = "模板类型",required = true)
    @NotNull(message = "模板类型不能为空")  
	private Integer templateType;

    /**
     * 司机
     */
	@ApiModelProperty(value = "司机",required = true)
    @NotNull(message = "司机不能为空")  
	@Min(value=1, message="driver不能小于1")
    private Long driver;

    /**
     * 护卫A
     */
	@ApiModelProperty(value = "护卫A",required = false)
//    @NotNull(message = "护卫A不能为空")  
//	@Min(value=1, message="securityA不能小于1")
    private Long securityA;

    /**
     * 护卫B
     */
	@ApiModelProperty(value = "护卫B",required = false)
//    @NotNull(message = "护卫B不能为空")  
//	@Min(value=1, message="securityB不能小于1")
    private Long securityB;

    /**
     * 业务-钥匙员
     */
	@ApiModelProperty(value = "业务-钥匙员",required = false)
//    @NotNull(message = "业务-钥匙员不能为空")  
//	@Min(value=1, message="routeKeyMan不能小于1")
    private Long routeKeyMan;

    /**
     * 业务-密码员
     */
	@ApiModelProperty(value = "业务-密码员",required = false)
//    @NotNull(message = "业务-清机员不能为空")  
//	@Min(value=1, message="routeOperMan不能小于1")
    private Long routeOperMan;

    /**
     * 跟车人员
     */
	@ApiModelProperty(value = "跟车人员",required = false)
//	@Min(value=1, message="follower不能小于1")
    private Long follower;


    /**
     * 备注
     */
	@ApiModelProperty(value = "备注",required = false)
    @Size(max=64,message="comments最大长度为64")
    private String comments;

}
