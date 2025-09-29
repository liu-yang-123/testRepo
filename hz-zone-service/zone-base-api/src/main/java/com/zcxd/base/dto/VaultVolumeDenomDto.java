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
public class VaultVolumeDenomDto implements Serializable {

    /**
     * 券别ID
     */
    private Long denomId;

    /**
     * 券别类型
     */
    private Integer denomType;

    /**
     * 券别名称
     */
    private String denomName;

    /**
     * 金额
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal amount;

    /**
     * 张数
     */
    private Long count;
}
