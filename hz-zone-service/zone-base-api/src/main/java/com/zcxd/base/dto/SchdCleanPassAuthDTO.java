package com.zcxd.base.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * 
 * @ClassName SchdCleanPassAuthDTO
 * @Description 清机员工通行备案
 * @author shijin
 * @Date 2021年7月5日下午3:41:10
 */
@ApiModel("员工休息配置")
@Data
public class SchdCleanPassAuthDTO {

    private Long id;

    private Long bankId;

	private String bankName;

	private Long empId;

	private String empName;

    private Integer passType;

	private String passCode;

	/**
	 * 线路编号
	 */
	private String routeNo;

	/**
	 * 岗位类型
	 */
	private Integer jobType;

	private Long createTime;

	private Long updateTime;

	private String createUserName;

	private String updateUserName;
}
