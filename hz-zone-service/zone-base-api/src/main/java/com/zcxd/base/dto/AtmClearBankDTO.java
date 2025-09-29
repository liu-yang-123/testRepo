package com.zcxd.base.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author songanwei
 * @date 2021-06-17
 */
@Data
public class AtmClearBankDTO implements Serializable {

    /**
     * 任务日期
     */
    private String taskDate;

    /**
     * 银行机构ID
     */
    private Long bankId;

    /**
     * 银行机构名称
     */
    private String bankName;

    /**
     * 计划清分
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal planAmount;

    /**
     * 实际清分
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal clearAmount;

    /**
     * 总任务数
     */
    private Integer totalTask;

    /**
     * 已完成任务数
     */
    private Integer doneTask;
}
