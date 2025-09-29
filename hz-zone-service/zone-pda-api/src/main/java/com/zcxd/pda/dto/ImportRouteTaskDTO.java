package com.zcxd.pda.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;


/**
 * 线路导入DTO
 */
@Data
public class ImportRouteTaskDTO {

    @ApiModelProperty(value = "线路组号",required = true)
    @NotBlank(message = "线路组号不能为空")
    @Size(max=3,message="线路组号最大长度为3")
    private String routeNo;//线路编号

    @ApiModelProperty(value = "银行编号",required = true)
    @NotBlank(message = "银行编号不能为空")
    @Size(max=16,min = 8,message="银行编号最大长度为16")
    private String bankNo; //银行编号

    @ApiModelProperty(value = "任务日期",required = true)
    @NotBlank(message = "任务日期不能为空")
    @Size(max=11,min = 11,message="任务日期最大长度为11")
    private String routeDate; //线路日期

    @ApiModelProperty(value = "加钞柜员")
    private String tellerNo; //加钞柜员

    @ApiModelProperty(value = "任务列表",required = true)
    @NotNull(message = "任务列表不能为空")
    private List<ImportAtmTaskItem> atmTaskItems; //atm任务列表
}
