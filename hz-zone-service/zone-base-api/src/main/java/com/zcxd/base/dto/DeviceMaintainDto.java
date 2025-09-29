package com.zcxd.base.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * @author songanwei
 * @date 2021-05-14
 */
@Data
public class DeviceMaintainDto {

    /**
     * 自增id
     */
    private Long id;

    /**
     * 设备编号
     */
    private Long deviceId;

    /**
     * 设备编号
     */
    private String deviceNo;

    /**
     * 维保日期
     */
    private Long mtDate;

    /**
     * 维保类型
     */
    private String mtType;

    /**
     * 故障原因
     */
    private String mtReason;

    /**
     * 维保内容
     */
    private String mtContent;

    /**
     * 维保成本
     */
    private BigDecimal mtCost;

    /**
     * 维保结果: 1 - 维修成功，0- 维修失败
     */
    private Integer mtResult;

    /**
     * 维保工程师
     */
    private String mtEngineer;

}
