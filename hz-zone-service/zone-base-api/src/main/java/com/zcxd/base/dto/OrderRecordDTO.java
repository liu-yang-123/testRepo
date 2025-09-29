package com.zcxd.base.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author songanwei
 * @date 2021-08-22
 */
@Data
public class OrderRecordDTO {

    /**
     * 库存类型:可用券，残损券，五好券
     */
    private Integer denomType;

    /**
     * 券别
     */
    private Long denomId;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 张数
     */
    private Integer count;

    /**
     * 备注
     */
    private String comments;

}
