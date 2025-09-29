package com.zcxd.base.vo;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 完成清分任务VO
 * @ClassName TradeClearTaskFinishVO
 * @Description TODO
 * @author 秦江南
 * @Date 2022年5月24日下午3:40:22
 */
@Data
public class TradeClearTaskFinishVO {
    /**
     * 订单ID
     */
    @ApiModelProperty("订单ID")
    private Long id;

    /**
     * 列表数据
     */
    @ApiModelProperty("券别记录数据")
    List<TradeClearTaskRecordVO>  taskRecordList;
}
