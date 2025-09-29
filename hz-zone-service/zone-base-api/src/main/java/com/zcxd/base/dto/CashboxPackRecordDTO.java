package com.zcxd.base.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CashboxPackRecordDTO {
	
    /**
     * 自增
     */
    private Long id;
    
	/**
     * 装盒时间（清分时间）
     */
    private Long packTime;

    /**
     * 任务日期（原钞盒回来再次入库）
     */
    private Long taskDate;
    
    /**
     * 清分员
     */
    private String clearManName;

    /**
     * 复核员
     */
    private String checkManName;

    /**
     * 钞盒
     */
    private String boxNo;

    /**
     * 清分机
     */
    private String deviceNo;

    /**
     * 券别id
     */
    private String denomName;

    /**
     * 包装金额
     */
    private BigDecimal amount;
    
    /**
     * 所属银行
     */
    private String bankName;
    
    /**
     * 钞盒状态
     */
    private Integer statusT;
}
