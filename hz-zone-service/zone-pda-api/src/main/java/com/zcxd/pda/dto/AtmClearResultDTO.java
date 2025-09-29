package com.zcxd.pda.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * atm清分结果DTO
 */
@Data
public class AtmClearResultDTO {

    @ApiModelProperty(value = "清分任务id",required = true)
    @NotNull(message = "清分任务id")
    private Long taskId;

    @ApiModelProperty(value = "线路id",required = true)
    @NotNull(message = "缺少线路id")
    private Long routeId;

    @ApiModelProperty(value = "清机任务id",required = true)
    @NotNull(message = "缺少清机任务id")
    private Long atmTaskId;

    @ApiModelProperty(value = "清分设备编码",required = true)
    @NotBlank(message = "缺少设备编码")
    private String deviceNo;

    @ApiModelProperty(value = "实际清分金额",required = true)
    @NotNull(message = "缺少实际清分金额")
    private BigDecimal clearAmount;

    @ApiModelProperty(value = "清分员",required = true)
    @NotNull(message = "缺少清分员")
    private Long clearMan;

    @ApiModelProperty(value = "复核员",required = true)
    @NotNull(message = "缺少复核员")
    private Long checkMan;

    @ApiModelProperty(value = "差错类型（0 - 无差错，1 - 长款，2 - 短款）",required = true)
    @NotNull(message = "差错类型")
    private Integer errorType;

    //private String errorReason;
    @ApiModelProperty(value = "差错复核主管")
    private Long errorConfirmMan;

    @ApiModelProperty(value = "假钞明细信息")
    private List<AtmClearErrorDTO> fakeErrorList;
    @ApiModelProperty(value = "残钞明细信息")
    private List<AtmClearErrorDTO> badErrorList;
    @ApiModelProperty(value = "夹张明细信息")
    private List<AtmClearErrorDTO> carryErrorList;
}
