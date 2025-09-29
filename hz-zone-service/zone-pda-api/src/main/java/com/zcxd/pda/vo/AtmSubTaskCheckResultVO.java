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
@ApiModel("ATM巡检结果VO")
@Data
public class AtmSubTaskCheckResultVO implements Serializable {

    @ApiModelProperty(value = "检查结果",required = true)
    private Map<String,Integer> checkResult;

    @ApiModelProperty(value = "处理备注说明")
    private String comments;
}
