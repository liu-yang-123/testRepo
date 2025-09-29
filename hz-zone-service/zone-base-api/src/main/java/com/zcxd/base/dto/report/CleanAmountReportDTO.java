package com.zcxd.base.dto.report;

import lombok.Data;
import java.math.BigDecimal;

/**
 * @author songanwei
 * @date 2021-10-14
 */
@Data
public class CleanAmountReportDTO {

    /**
     * 银行机构ID
     */
    private Long bankId;

    /**
     * 银行机构 名称
     */
    private String bankName;

    /**
     * 清分金额
     */
    private BigDecimal cleanAmount;
    
    /**
     * 清分数量
     */
    private Long cleanCount;

    /**
     * 长款笔数
     */
    private Long moreNumber;

    /**
     * 长款金额
     */
    private BigDecimal moreAmount;

    /**
     * 短款笔数
     */
    private Long lessNumber;

    /**
     * 短款金额
     */
    private BigDecimal lessAmount;

    /**
     * 假疑币笔数
     */
    private Long falseNumber;

    /**
     * 假疑币金额
     */
    private BigDecimal falseAmount;

    /**
     * 张数
     */
    private Long bringNumber;

    /**
     * 金额
     */
    private BigDecimal bringAmount;

    /**
     * 残缺币张数
     */
    private Long missNumber;

    /**
     * 残缺币金额
     */
    private BigDecimal missAmount;

}
