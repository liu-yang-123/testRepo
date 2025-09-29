package com.zcxd.pda.dto;

import lombok.Data;

@Data
public class BoxpackDTO {
    /**
     * 唯一标识
     */
    private Long id;
   
    /**
     * RFID卡号
     */
    private String rfid;
    
    /**
     * 箱包编号
     */
    private String boxNo;

    /**
     * 箱包名称
     */
    private String boxName;

    /**
     * 箱包状态: 1 - 网点，2 - 途中，3 - 库房
     */
    private Integer statusT;

}
