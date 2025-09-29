package com.zcxd.db.model.result;

import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * 清分结果明细查询返回
 * </p>
 *
 * @author admin
 * @since 2022-05-13
 */
@Data
public class TradeClearRecordQueryResult {


    private Long id;
    /**
     * 事业部ID
     */
    private Long departmentId;

    /**
     * 银行机构
     */
    private Long bankId;

    /**
     * 区域清分任务id
     */
    private Long taskId;

    /**
     * 券别
     */
    private Long denomId;

    /**
     * 清分标志：0 - 可用券，1 - 残损券，2 - 五好券, 3 - 未清分, 4 - 尾零钞
     */
    private Integer gbFlag;

    /**
     * 总金额
     */
    private BigDecimal amount;
    
    /**
     * 张数
     */
    private Integer count;
}
