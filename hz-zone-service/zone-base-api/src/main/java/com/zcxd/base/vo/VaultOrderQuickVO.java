package com.zcxd.base.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author songanwei
 * @date 2021-06-04
 */
@ApiModel("快捷出库视图对象")
@Data
public class VaultOrderQuickVO implements Serializable {

    /**
     * 区域ID
     */
    @ApiModelProperty(value = "部门ID", required = true)
    private Long departmentId;
    /**
     * 日期
     */
    private Long orderDate;

    /**
     * 订单类型
     */
    @ApiModelProperty("订单类型")
    private Integer orderType;

    /**
     * 银行机构ID
     */
    @ApiModelProperty("银行机构ID")
    private Long bankId;

}
