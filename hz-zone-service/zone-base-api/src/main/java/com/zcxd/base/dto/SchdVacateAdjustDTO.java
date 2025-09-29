package com.zcxd.base.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * 
 * @ClassName SchdVacateAdjustDTO
 * @Description 员工休息调整
 * @author shijin
 * @Date 2021年7月19日下午3:41:10
 */
@ApiModel("员工休息调整")
@Data
public class SchdVacateAdjustDTO {

    private Long id;
	private String adjustDate;
	private Integer adjustType;
	private Long empId;
	private String empName;
	private Long repEmpId;
	private String repEmpName;
	private String reason;
	private Long createTime;
	private Long updateTime;
	private String createUserName;
	private String updateUserName;
}
