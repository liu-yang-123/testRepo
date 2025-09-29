package com.zcxd.base.vo;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 清分任务添加VO
 * @ClassName TradeClearTaskAddVO
 * @Description TODO
 * @author 秦江南
 * @Date 2022年5月23日下午5:21:13
 */
@Data
public class TradeClearTaskAddVO {

    /**
     * 订单ID
     */
    @ApiModelProperty("订单ID")
    private Long id;

    /**
     * 区域ID
     */
    @ApiModelProperty(value = "区域ID", required = true)
    @NotNull(message = "区域ID不能为空")
    private Long departmentId;
    /**
     * 日期
     */
    @ApiModelProperty(value = "日期", required = true)
    @NotNull(message = "日期不能为空")
    private Long taskDate;

    /**
     * 清分类型
     */
    @ApiModelProperty(value = "清分类型", required = true)
    @NotNull(message = "清分类型不能为空")
    private Integer clearType;

    /**
     * 银行机构ID
     */
    @ApiModelProperty(value = "银行机构ID", required = true)
    @NotNull(message = "银行机构ID不能为空")
    private Long bankId;
    
    /**
     * 券别明细
     */
    @ApiModelProperty(value = "券别明细", required = true)
    @NotNull(message = "券别明细不能为空")
    private Integer haveDetail;

    
    /**
     * 任务金额
     */
    @ApiModelProperty("任务金额")
    private BigDecimal totalAmount;
    
    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String comments;
    
    /**
     * 列表数据
     */
    @ApiModelProperty("券别记录数据")
    List<TradeClearTaskRecordVO>  taskRecordList;

}
