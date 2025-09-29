package com.zcxd.base.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 线路备用金额VO
 * </p>
 *
 * @author admin
 * @since 2021-08-11
 */
@ToString
@Data
public class AtmAdditionCashVO implements Serializable {

    private Long id;
    /**
     * 日期
     */
    @ApiModelProperty(name="任务日期",required = true)
    private Long taskDate;
    /**
     * 线路
     */
    @ApiModelProperty(name="备用金所属线路",required = true)
    private Long routeId;
    /**
     * 所属机构
     */
    @ApiModelProperty(name="备用金所属机构",required = true)
    private Long bankId;
    /**
     * 券别(100, 10)
     */
    @ApiModelProperty(name="备用金券别",required = true)
    private Integer denomValue;
    /**
     * 备用金金额
     */
    @ApiModelProperty(name="备用金金额",required = true)
    private BigDecimal amount;

    /**
     * 金额类型
     */
    @ApiModelProperty(name = "金额类型", required = true)
    private Integer cashType;
    
    /**
     * 执行线路id
     */
    @ApiModelProperty(name="执行线路",required = true)
    private Long carryRouteId;

    /**
     * 备注
     */
    @ApiModelProperty(name = "备注")
    private String comments;
}
