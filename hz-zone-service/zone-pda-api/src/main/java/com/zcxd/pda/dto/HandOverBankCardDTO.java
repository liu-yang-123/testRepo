package com.zcxd.pda.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 交接卡信息DTP
 */
@Data
public class HandOverBankCardDTO {

    /**
     * 线路id
     */
    private Long routeId;
    /**
     * 线路编号
     */
    private String routeNo;
    /**
     * 线路名称
     */
    private String routeName;
    /**
     * 钥匙员
     */
    private String keyManName;
    /**
     * 密码员
     */
    private String opManName;

    List<AtmTaskCardGroupItem> cardList;
}
