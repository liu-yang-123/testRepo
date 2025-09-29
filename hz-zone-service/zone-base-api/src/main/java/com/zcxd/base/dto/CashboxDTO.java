package com.zcxd.base.dto;

import lombok.Data;

/**
 * 
 * @ClassName CashboxDTO
 * @Description 钞盒信息
 * @author 秦江南
 * @Date 2021年5月24日上午9:51:18
 */
@Data
public class CashboxDTO {
    /**
     * 自增
     */
    private Long id;

    /**
     * 编号
     */
    private String boxNo;

    /**
     * 关联RFID
     */
    private String rfid;

    /**
     * 钞盒钞袋( 1 - 钞盒，2 - 钞袋）
     */
    private Integer boxType;
    /**
     * 是否已使用（0 - 未使用，1 - 已使用)
     */
    private Integer used;

    /**
     * 状态(停用，使用）
     */
    private Integer statusT;
}
