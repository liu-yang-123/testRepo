package com.zcxd.base.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 
 * @ClassName SchdDriverAssignDTO
 * @Description 司机主替班管理
 * @author shijin
 * @Date 2021年7月5日下午3:41:10
 */
@ApiModel("候选分配管理")
@Data
public class SchdAlternateAssignDTO {
	/**
	 * 主键ID
	 */
    private Long id;
	/**
	 * 计划ID
	 */
	private Long planId;

	/**
	 * 计划名称
	 */
	private String planName;

	/**
	 * 操作类别
	 */
	private Integer planType;

	/**
	 * 主备班类型
	 */
	private Integer alternateType;

	/**
	 * 员工ID
	 */
	private Long employeeId;

	/**
	 * 员工名称
	 */
	private String employeeName;
	/**
	 * 车辆编号字符串
	 */
	private String vehicleNos;

	/**
	 * 线路ID字符串
	 */
	private String routeIds;

	/**
	 * 线路编号字符串
	 */
	private String routeNos;
	/**
	 * 创建时间
	 */
	private Long createTime;

	/**
	 * 更新时间
	 */
	private Long updateTime;
}
