package com.zcxd.pda.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author shijin
 * @date 2021/5/21 9:30
 */
@ApiModel("巡检项目结果")
@Data
public class CheckItemResult implements Serializable {

    @ApiModelProperty(value = "细项名称")
    private String name;

    @ApiModelProperty(value = "检查结果( 0 or 1")
    private Integer result;
}
