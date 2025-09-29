package com.zcxd.base.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 修改吞没卡信息VO
 */
@Data
public class BankCardEditVO {
    /**
     * 吞没卡id
     */
    @ApiModelProperty(value = "吞没卡id")
    private Long id;
    
    /**
     * 发卡行
     */
    @ApiModelProperty(value = "发卡行")
    private String cardBank;

    /**
     * 吞卡卡号
     */
    @ApiModelProperty(value = "吞卡卡号")
    private String cardNo;


}
