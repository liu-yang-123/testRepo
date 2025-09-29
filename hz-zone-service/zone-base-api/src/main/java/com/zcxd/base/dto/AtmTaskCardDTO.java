package com.zcxd.base.dto;

import lombok.Data;

/**
 * 
 * @ClassName AtmTaskCardDTO
 * @Description 吐卡记录DTO
 * @author 秦江南
 * @Date 2021年7月25日下午5:32:47
 */
@Data
public class AtmTaskCardDTO{

    /**
     * 唯一标识
     */
    private Long id;

    /**
     * 取卡线路
     */
    private String routeNo;

    private Long bankId;
    /**
     * 所属机构
     */
    private String bankName;

    /**
     * 设备编号
     */
    private String atmTerNo;

    /**
     * 吞卡卡号
     */
    private String cardNo;

    /**
     * 发卡行
     */
    private String cardBank;
    
    /**
     * 卡类型（ 0 - 实物卡，1 - 现场拿卡单）
     */
    private Integer category;

    /**
     * 取回日期
     */
    private String retriveDay;
    /**
     * 交接人a
     */
    private String collectManAName;

//    /**
//     * 交接人b
//     */
//    private String collectManBName;

    /**
     * 交接时间
     */
    private Long collectTime;

    /**
     * 配卡出库人
     */
    private String dispatchManAName;

//    /**
//     * 配卡人2
//     */
//    private String dispatchManBName;

    /**
     * 配卡时间
     */
    private Long dispatchTime;

    /**
     * 送卡线路
     */
    private String deliverRouteNo;

    private Long deliverBankId;
    /**
     * 交卡网点
     */
    private String deliverBankName;

    /**
     * 上交日期
     */
    private String deliverDay;

    /**
     * 派送方式（0 - 上缴银行，1 - 自取）
     */
    private Integer deliverType;

    /**
     * 移交人证件号码
     */
    private String receiverIdno;

    /**
     * 移交人姓名
     */
    private String receiverName;

    /**
     * 移交时间
     */
    private Long receiveTime;

    /**
     * 状态（0 - 取回，1-入库，2 - 派送, 3 - 领取
     */
    private Integer statusT;

    /**
     * 备注
     */
    private String comments;

}
