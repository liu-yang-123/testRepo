package com.zcxd.base.dto;

import lombok.Data;

/**
 * 
 * @ClassName ATMDTO
 * @Description ATM设备信息
 * @author 秦江南
 * @Date 2021年5月27日下午6:37:30
 */
@Data
public class ATMDTO {

    /**
     * 自增
     */
    private Long id;

    /**
     * 终端编号
     */
    private String terNo;

    /**
     * 设备类型
     */
    private String terType;

    /**
     * 设备品牌
     */
    private String terFactory;

    /**
     * 加钞券别
     */
    private Integer denom;
    
    /**
     * 位置类型（离行式..)
     */
    private Integer locationType;

    /**
     * 状态
     */
    private Integer statusT;

    /**
     * 所属机构
     */
    private Long bankId;
    
    /**
     * 所属机构名称
     */
    private String bankName;
    
    /**
     * 所属机构编号
     */
    private String bankNo;
    
    /**
     * 取吞卡网点
     */
    private Long gulpBankId;
    
    /**
     * 取吞卡网点名称
     */
    private String gulpBankName;
    
    /**
     * 装机信息
     */
    private String installInfo;
    
    /**
     * 备注
     */
    private String comments;

}
