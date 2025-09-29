package com.zcxd.base.vo.excel;

import lombok.Data;
import java.math.BigDecimal;

/**
 * @author songanwei
 * @date 2021-07-05
 */
@Data
public class ClearAtmListVO {

    /**
     * 设备编号
     */
    private String deviceNo;

    /**
     * 系统库存金额
     */
    private BigDecimal amount;


}
