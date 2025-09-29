package com.zcxd.pda.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author shijin
 * @date 2021/5/21 9:30
 */
@ApiModel("ATM维护任务结果VO")
@Data
public class AtmSubTaskRepairResultVO implements Serializable {

//    @ApiModelProperty(value = "维修子任务id",required = true)
//    @NotNull(message = "缺少任务id")
//    private Long subTaskId;

    @ApiModelProperty(value = "厂商到达时间")
    private Long engineerArriveTime;

    @ApiModelProperty(value = "故障部位")
    private String faultType;

    @ApiModelProperty(value = "故障描述")
    private String description;

    @ApiModelProperty(value = "问题修改结果")
    private Integer dealResult;

    @ApiModelProperty(value = "处理备注说明")
    private String dealComments;

    @ApiModelProperty(value = "维修人员")
    private String engineerName;

    @ApiModelProperty(value = "是否更换钞箱")
    private Integer cashboxReplace;

    @ApiModelProperty(value = "是否有钞票遗留钞箱")
    private Integer cashInBox;
}
