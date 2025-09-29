package com.zcxd.pda.vo;

import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName BoxpackTaskReportVO
 * @Description 早送晚收任务申报
 * @author 秦江南
 * @Date 2021年11月29日上午10:00:07
 */
@ApiModel("早送晚收任务申报")
@Data
public class BoxpackTaskReportVO {

    @ApiModelProperty(value = "网点id",required = true)
    @NotNull(message = "缺少网点id")
    private Long bankId;
    @ApiModelProperty(value = "任务类型",required = true)
    @NotNull(message = "缺少任务类型")
    private Integer taskType;
    @ApiModelProperty(value = "任务日期",required = true)
    @NotNull(message = "缺少任务日期")
    private String taskDate;
    @ApiModelProperty(value = "箱包列表",required = true)
    @NotNull(message = "箱包列表不能为空")
    private List<Long> boxpacks;
}
