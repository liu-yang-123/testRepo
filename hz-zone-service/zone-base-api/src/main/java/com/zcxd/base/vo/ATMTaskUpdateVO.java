//package com.zcxd.base.vo;
//
//
//import java.math.BigDecimal;
//
//import javax.validation.constraints.NotNull;
//import javax.validation.constraints.Size;
//
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
//import lombok.Data;
//
///**
// * 
// * @ClassName ATMTaskUpdateVO
// * @Description 修改ATM任务
// * @author 秦江南
// * @Date 2021年6月22日下午3:54:33
// */
//@ApiModel("修改ATM任务")
//@Data
//public class ATMTaskUpdateVO {
//	
//    /**
//     * 自增
//     */
//	@ApiModelProperty(value = "ATM任务Id",required = false)
//    private Long id;
//	
//	/**
//	 * 线路id
//	 */
//	@ApiModelProperty(value = "线路id",required = true)
//	@NotNull(message = "线路id不能为空")
//	private Long routeId;
//
//	/**
//	 * 任务日期
//	 */
//	@ApiModelProperty(value = "任务日期",required = true)
//	@NotNull(message = "任务日期不能为空")
//	private Long taskDate;
//	
//	/**
//	 * ATM设备id
//	 */
//	@ApiModelProperty(value = "ATM设备id",required = true)
//	@NotNull(message = "ATM设备id不能为空")
//	private Long atmId;
//
//	/**
//	 * 任务类型
//	 */
//	@ApiModelProperty(value = "任务类型",required = true)
//	@NotNull(message = "任务类型不能为空")
//	private Integer taskType;
//	
//	/**
//	 * 加钞金额
//	 */
//	@ApiModelProperty(value = "加钞金额",required = true)
//	@NotNull(message = "加钞金额不能为空")
//	private BigDecimal amount;
//	
//	/**
//	 * 任务备注
//	 */
//	@ApiModelProperty(value = "任务备注",required = false)
//	@Size(max=64,message="任务备注最大长度为64")
//	private String comments;
//	
//}
