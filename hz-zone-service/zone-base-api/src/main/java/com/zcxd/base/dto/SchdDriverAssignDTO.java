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
@ApiModel("司机主替班管理")
@Data
public class SchdDriverAssignDTO {

    private Long id;
    private Long driver;
	private String driverName;
	private Long routeId;
	private String routeNo;
	private String routeName;
    private Integer driverType;
	private Integer assignType;
	private String vehicleNo;

	private Long createTime;
	private Long updateTime;
	private String createUserName;
	private String updateUserName;
}
