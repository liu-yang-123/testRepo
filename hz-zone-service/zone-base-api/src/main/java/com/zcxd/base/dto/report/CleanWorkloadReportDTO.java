package com.zcxd.base.dto.report;

import lombok.Data;
import java.math.BigDecimal;

/**
 * @author songanwei
 * @date 2021-10-14
 */
@Data
public class CleanWorkloadReportDTO {

    /**
     * 序号
     */
    private Integer index;

    /**
     * 姓名
     */
    private String name;

    /**
     * 清点金额
     */
    private BigDecimal amount;

    /**
     * 差错笔数
     */
    private Long number;

}
