package com.zcxd.base.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author
 * @param
 * @return
 */
@Data
public class SchdResultRowDTO {

    @ApiModelProperty(value = "员工ID")
    private Long empId;

    @ApiModelProperty(value = "部门")
    private Long departmentId;

    @ApiModelProperty(value = "日期")
    private Long planDay;

    @ApiModelProperty(value = "线路")
    private String routeNo;



}