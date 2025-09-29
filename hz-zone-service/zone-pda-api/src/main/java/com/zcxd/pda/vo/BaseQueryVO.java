package com.zcxd.pda.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author shijin
 * @date 2021/5/18 10:17
 */
@ApiModel("分页查询类")
@Data
public class BaseQueryVO implements Serializable {

    /**
     * 用户类型
     */
    @ApiModelProperty(value = "页码",required = true)
    private Integer page;

    /**
     * 指纹序号
     */
    @ApiModelProperty(value = "每页记录数",required = true)
    @NotNull(message = "limit不能为空")
    private Integer limit;
}
