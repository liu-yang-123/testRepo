package com.zcxd.base.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 吞没卡派送设置DTO
 */
@Data
public class BankCardDeliverSettingVO {
    /**
     * 送卡网点
     */
    @ApiModelProperty(value = "吞没卡id")
    private Long id;
    /**
     * 送卡网点
     */
    @ApiModelProperty(value = "送卡网点")
    private Long deliverBankId;
    
    /**
     * 送卡线路
     */
    @ApiModelProperty(value = "送卡线路")
    private String deliverRouteNo;

    /**
     * 送卡日期
     */
    @ApiModelProperty(value = "送卡日期")
    private Long deliverDay;

}
