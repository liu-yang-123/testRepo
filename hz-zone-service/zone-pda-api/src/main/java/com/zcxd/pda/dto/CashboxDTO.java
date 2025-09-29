package com.zcxd.pda.dto;

import lombok.Data;

/**
 * 钞盒信息DTO
 */
@Data
public class CashboxDTO {
    private String boxNo;   //钞盒编号
    private String rfid; //钞盒rfid
}
