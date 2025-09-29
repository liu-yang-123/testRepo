package com.zcxd.base.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author shijin
 * @date 2022-05-30
 */
@ApiModel("ATM清分任务完成对象")
@Data
public class ClearTaskFinishVO implements Serializable {

    /**
     * 清分任务id
     */
    @ApiModelProperty("主键ID")
    @NotNull(message = "缺少参数id")
    private Long id;

    /**
     * 清分员
     */
    @NotNull(message = "缺少清分员")
    private Long clearMan;
    /**
     * 复核员
     */
    @NotNull(message = "缺少复核员")
    private Long checkMan;

    /**
     * 实际清分金额
     */
    @NotNull(message = "缺少实际清分金额")
    private BigDecimal clearAmount;

    /**
     * 差错复核主管
     */
    private Long errorConfirmMan;

    /**
     * 清分差错数据列表
     */
    List<AtmClearErrorVO> errorList;
}
