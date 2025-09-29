package com.zcxd.base.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("线路排班结果下拉人员数据类")
public class EmployeeSelectDTO {
    private  Long id;
    private  String empName;
}