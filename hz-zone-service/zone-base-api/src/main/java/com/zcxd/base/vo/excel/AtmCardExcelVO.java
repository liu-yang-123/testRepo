package com.zcxd.base.vo.excel;

import lombok.Data;

/**
 * @author songanwei
 * @date 2021-08-26
 */
@Data
public class AtmCardExcelVO {

    /**
     * 序号
     */
    private Integer index;

    /**
     * 卡号
     */
    private String cardNo;

    /**
     * 是否昨日吞卡
     */
    private String isYesterday;

    /**
     * 卡类型（ 0 - 实物卡，1 - 现场拿卡单）
     */
    private String category;

    /**
     * 派送方式（0 - 上缴银行，1 - 自取）
     */
    private String deliverType;
    
    /**
     * 发卡行
     */
    private String cardBank;

    /**
     * ATM设备编号
     */
    private String atmNo;

    /**
     * 带回线路编号
     */
    private String routeNo;

    /**
     * 交送线路
     */
    private String deliveryRouteNo;


    /**
     * 接收网点
     */
    private String address;

    /**
     * 接收日期
     */
    private String date;

    /**
     * 制表人
     */
    private String makeMan;

    /**
     * 复核人
     */
    private String checkMan;

    /**
     * 移交日期
     */
    private String deliverDate;

    /**
     * 备注
     */
    private String comments;

    /**
     * 接收人
     */
    private String acceptMan;

}
