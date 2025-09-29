package com.zcxd.pda.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author shijin
 * @date 2021/5/21 9:30
 */
@ApiModel("ATM加钞任务结果VO")
@Data
public class AtmSubTaskCleanResultVO implements Serializable {

    @ApiModelProperty(value = "加钞钞盒列表")
    private List<UsedBoxVO> usedBoxList;

    @ApiModelProperty(value = "回笼钞盒/钞袋列表")
    private List<String> returnBoxList;

    @ApiModelProperty(value = "即时清点标志")
    private Integer clearSite;
}
