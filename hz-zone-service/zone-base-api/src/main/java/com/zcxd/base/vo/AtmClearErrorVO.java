package com.zcxd.base.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author songanwei
 * @date 2021-08-05
 */
@Data
public class AtmClearErrorVO {

    /**
     * 差错明细类别：1-假币 2-残缺币 3-夹张
     */
    private Integer detailType;

    /**
     * 券别
     */
    private Long denomId;

    /**
     * 张数
     */
    private Integer count;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 冠字号
     */
    private String rmbSn;

}
