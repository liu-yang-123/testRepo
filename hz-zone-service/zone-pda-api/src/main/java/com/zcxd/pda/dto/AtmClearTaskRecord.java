package com.zcxd.pda.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * atm任务详情DTO
 */
@Data
public class AtmClearTaskRecord {

    /**
     * taskid
     */
    private Long id;

    /**
     * 设备编号
     */
    private String atmNo;

    /**
     * 设备编号
     */
    private String comments;

    /**
     * 库存金额
     */
    private BigDecimal planAmount;

    /**
     * 清点金额
     */
    private BigDecimal clearAmount;

    /**
     * 差额
     */
    private BigDecimal errorAmount;
    /**
     * 任务状态
     */
    private Integer statusT;

}
