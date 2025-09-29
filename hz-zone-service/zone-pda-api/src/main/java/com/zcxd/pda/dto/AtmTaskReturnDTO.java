package com.zcxd.pda.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * atm回笼钞袋钞盒关联信息DTO
 */
@Data
public class AtmTaskReturnDTO {
    /**
     * 关联的RFID
     */
    private String boxRfidQrno;

    /**
     * atmid
     */
    private Long atmId;
    /**
     * 线路id
     */
    private Long routeId;
}
