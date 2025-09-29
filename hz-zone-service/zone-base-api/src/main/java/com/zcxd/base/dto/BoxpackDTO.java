package com.zcxd.base.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * @author songanwei
 * @date 2021-11-23
 */
@Data
public class BoxpackDTO  implements Serializable {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 箱包编号
     */
    private String boxNo;

    /**
     * 箱包名称
     */
    private String boxName;

    /**
     * 机构ID
     */
    private Long bankId;

    /**
     * 公用机构
     */
    private Long shareBankId;

    /**
     * rfid编号
     */
    private String rfid;

    /**
     * 状态 箱包状态: 1 - 网点，2 - 途中，3 - 库房
     */
    private Integer statusT;

    /**
     * 备注
     */
    private String comments;

    /**
     * 机构名称
     */
    private String bankName;

    /**
     * 共享机构
     */
    private String shareBankName;
}
