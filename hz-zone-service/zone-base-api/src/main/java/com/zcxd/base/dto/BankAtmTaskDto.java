package com.zcxd.base.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * @author songanwei
 * @date 2021-06-28
 */
@Data
public class BankAtmTaskDto {
    /**
     * 银行机构ID
     */
    private Long bankId;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 已确认总金额
     */
    private BigDecimal confirmAmount;

    /**
     * 已建出库金额
     */
    private BigDecimal outAmount;

    /**
     * 待确认总金额
     */
    private BigDecimal pendingAmount;


    /**
     * 确认备用金金额
     */
    private BigDecimal cashAmount;


    /**
     * 已确认任务数据
     */
    private Integer confirmTaskCount;

    /**
     * 已建出库任务数据
     */
    private Integer outTaskCount;

    /**
     * 待确认任务数
     */
    private Integer pendingTaskCount;

    /**
     * 券别金额列表
     */
    private List<BankAtmDenomTaskDto> denomList;

}
