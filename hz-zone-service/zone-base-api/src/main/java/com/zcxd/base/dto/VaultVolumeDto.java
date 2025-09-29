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
public class VaultVolumeDto implements Serializable {

    /**
     * 机构ID
     */
    private long bankId;

    /**
     * 机构名称
     */
    private String bankName;


    /**
     * 金额
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal amount;
}
