package com.zcxd.base.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author songanwei
 * @date 2021-05-29
 */
@Data
public class VaultCheckDto implements Serializable {

    /**
     * 主键ID
     */
    private long id;

    /**
     * 机构ID
     */
    private long bankId;

    /**
     * 机构名称
     */
    private String bankName;

    /**
     * 可用券_余额
     */
    private BigDecimal usableBalance;

    /**
     * 残损券_余额
     */
    private BigDecimal badBalance;

    /**
     * 五好券_余额
     */
    private BigDecimal goodBalance;

    /**
     * 未清分余额
     */
    private BigDecimal unclearBalance;

    /**
     * 可用券清点金额
     */
    private BigDecimal usableAmount;

    /**
     * 残损券清点金额
     */
    private BigDecimal badAmount;

    /**
     * 五号券清点金额
     */
    private BigDecimal goodAmount;

    /**
     * 未清分清点金额
     */
    private BigDecimal unclearAmount;

    /**
     * 备注
     */
    private String comments;

    /**
     * 查库人1
     */
    private Long whOpMan;

    /**
     * 查库人1名称
     */
    private String whOpManName;

    /**
     * 查库人2
     */
    private Long whCheckMan;

    /**
     * 查库人2名称
     */
    private String whCheckManName;
    /**
     * 查库人3
     */
    private Long whConfirmMan;

    /**
     * 查库人3名称
     */
    private String whConfirmManName;

    /**
     * 盘点时间
     */
    private Long whOpTime;

    /**
     * 尾零余额
     */
    private BigDecimal remnantBalance;

    /**
     * 尾零盘点金额
     */
    private BigDecimal remnantAmount;

}
