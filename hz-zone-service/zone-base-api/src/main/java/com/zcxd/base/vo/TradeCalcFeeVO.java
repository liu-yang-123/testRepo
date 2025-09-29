package com.zcxd.base.vo;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;

@Data
public class TradeCalcFeeVO {

    @NotNull(message = "部门不能为空")
    private Long departmentId;
    private Integer year;
    private Integer month;
    private Integer day;
    private String beginDay;
    private String endDay;
    private Long bankId;
}
