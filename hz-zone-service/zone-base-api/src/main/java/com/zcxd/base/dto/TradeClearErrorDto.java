package com.zcxd.base.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 清分差错信息
 */
@Data
public class TradeClearErrorDto {
    private Long departmentId;
    private Long bankId; //错款银行
    private String subBank; //错款支行名
    private Long clearDate; //任务日期/发现日期
    private String sealDate; //封签日期
    private Long denomId;  //券别
    private Integer cashOverCount; //长款笔数
    private BigDecimal cashOverAmount; //长款金额
    private Integer cashShortCount; //短款笔数
    private BigDecimal cashShortAmount; //短款金额
    private Integer fakeCount; //假币笔数
    private BigDecimal fakeAmount; //假币金额
    private Integer carryCount; //夹张笔数
    private BigDecimal carryAmount; //夹张金额
    private String errorMan; //错款人
    private String clearMan; //发现人
    private String checkMan; //复核人
    private String sealMan; //复核人
    private String comments; //备注
    private String errorPhoto; //差错图片
}
