package com.zcxd.base.dto.report;

import lombok.Data;
import java.io.Serializable;

/**
 * @author songanwei
 * @date 2021-10-21
 */
@Data
public class BankCardReportDTO implements Serializable {

    /**
     * 序号
     */
    private Integer index;

    /**
     * 设备编号
     */
    private String terNo;

    /**
     * 吞没卡号
     */
    private String cardNo;

    /**
     * 发卡银行
     */
    private String cardBank;

    /**
     * 带回线路
     */
    private String routeNo;

    /**
     * 收卡日期
     */
    private String collectDate;

    /**
     * 交送线路
     */
    private String deliverRouteNo;

    /**
     * 送卡日期
     */
    private String deliverDate;

    /**
     * 接收网点
     */
    private String bankName;
}
