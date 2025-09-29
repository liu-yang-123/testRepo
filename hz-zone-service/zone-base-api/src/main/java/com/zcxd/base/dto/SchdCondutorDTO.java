package com.zcxd.base.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @ClassName SchdCondutorDTO
 */
@ApiModel("车长资格")
@Data
public class SchdCondutorDTO {
    @ApiModelProperty(value = "唯一标识")
    private Long id;

    @ApiModelProperty(value = "姓名")
    private String empName;

    @ApiModelProperty(value = "编号")
    private String empNo;

    @ApiModelProperty(value = "岗位")
    private String jobName;

    @ApiModelProperty(value = "车长资格（0 - 不具备车长资格，1 - 具备车长资格）")
    private Integer routeLeader;

    @ApiModelProperty(value = "部门")
    private Long departmentId;
}