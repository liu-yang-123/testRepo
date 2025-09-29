package com.zcxd.base.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author shijin
 * @date 2021-08-05
 */
@Data
public class AtmBankCheckRecordDTO implements Serializable {

    /**
     * 记录id
     */
    private Long id;
    private String routeNo;
    /**
     * 银行名称
     */
    private String bankName;
    /**
     * 银行机构名称
     */
    private String subBankName;

    /**
     * 巡检时间
     */
    private Long checkTime;
    /**
     * 加钞间检查结果
     */
    private Map<String,Integer> roomCheckResults;
    /**
     * 大厅检查结果
     */
    private Map<String,Integer> hallCheckResults;

    private Integer checkNormal; //检查结果是否正常
    /**
     * 撤防时间
     */
    private Long revokeAlarmTime;
    /**
     * 布防时间
     */
    private Long setAlarmTime;

    /**
     * 巡检备注
     */
    private String comments;
}
