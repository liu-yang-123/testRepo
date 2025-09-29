package com.zcxd.pda.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author shijin
 * @date 2021/5/21 9:30
 */
@ApiModel("ATM任务结果VO")
@Data
public class AtmTaskResultVO implements Serializable {

    @ApiModelProperty(value = "离线上送标志(1 - 离线，0 - 实时)",required = true)
    @NotNull(message = "缺少任务id")
    private Integer offline;

    @ApiModelProperty(value = "加钞任务id(0 - 临时任务，非0 - 排班任务)",required = true)
    @NotNull(message = "缺少任务id")
    private Long taskId;

    @ApiModelProperty(value = "线路id(临时任务时使用)")
    private Long routeId;

    @ApiModelProperty(value = "设备编号(临时任务时使用)")
    private String atmNo;

    @ApiModelProperty(value = "加钞开始时间",required = true)
    @NotNull(message = "缺少开始时间")
    private Long beginTime;

    @ApiModelProperty(value = "加钞结束时间",required = true)
    @NotNull(message = "缺少结束时间")
    private Long endTime;

    @ApiModelProperty(value = "设备运行状态",required = true)
    @NotNull(message = "缺少运行状态")
    private Integer atmRunStatus;

    @ApiModelProperty(value = "卡钞金额")
    private Integer stuckAmount;

    @ApiModelProperty(value = "吞卡列表")
    private List<BankCardInfo> bankCardInfos;

    @ApiModelProperty(value = "维修记录")
    private AtmSubTaskRepairResultVO repairResultVO;

    @ApiModelProperty(value = "清机记录")
    private AtmSubTaskCleanResultVO cleanResultVO;

    @ApiModelProperty(value = "设备检查记录")
    private AtmSubTaskCheckResultVO checkResultVO;

}
