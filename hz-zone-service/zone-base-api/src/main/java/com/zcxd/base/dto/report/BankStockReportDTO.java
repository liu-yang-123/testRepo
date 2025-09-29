package com.zcxd.base.dto.report;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author songanwei
 * @date 2021-10-19
 */
@Data
public class BankStockReportDTO implements Serializable {

    /**
     * 机构ID
     */
    private Long bankId;

    /**
     * 机构名称
     */
    private String bankName;

    /**
     * 日均库存
     */
    private BigDecimal average;

    /**
     * 最高库存
     */
    private BigDecimal high;

    /**
     * 最低库存
     */
    private BigDecimal low;
}
