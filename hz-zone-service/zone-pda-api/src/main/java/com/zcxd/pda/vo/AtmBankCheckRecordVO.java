package com.zcxd.pda.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author shijin
 * @date 2021/5/21 9:30
 */
@ApiModel("离行自助终端巡检结果VO")
@Data
public class AtmBankCheckRecordVO implements Serializable {

    @ApiModelProperty(value = "线路id",required = true)
    @NotNull(message = "缺少线路id")
    private Long routeId;

    @ApiModelProperty(value = "网点id",required = true)
    @NotNull(message = "缺少网点id")
    private Long subBankId;

    @ApiModelProperty(value = "巡检时间",required = true)
    @NotNull(message = "缺少巡检时间")
    private Long checkTime;

    @ApiModelProperty(value = "撤防时间",required = true)
    @NotNull(message = "缺少撤防时间")
    private Long revokeAlarmTime;

    @ApiModelProperty(value = "布防时间",required = true)
    @NotNull(message = "缺少布防时间")
    private Long setAlarmTime;

    @ApiModelProperty(value = "加钞间检查结果",required = true)
    private Map<String,Integer> roomCheckResult;

    @ApiModelProperty(value = "取款区域检查结果",required = true)
    private Map<String,Integer> hallCheckResult;

    @ApiModelProperty(value = "处理备注说明",required = true)
    private String comments;
}
