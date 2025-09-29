package com.zcxd.pda.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author songanwei
 * @date 2021/4/19 14:17
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel("用户登录")
@Data
public class VaultOrderQueryVO extends BaseQueryVO {

    /**
     * 日期
     */
    @ApiModelProperty(value = "订单日期",required = true)
    private Long orderDate;
    /**
     * 状态
     */
    @ApiModelProperty(value = "订单状态",required = true)
    private Integer status;

}
