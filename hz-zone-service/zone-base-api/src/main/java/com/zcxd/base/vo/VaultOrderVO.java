package com.zcxd.base.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author songanwei
 * @date 2021-06-04
 */
@ApiModel("订单视图对象")
@Data
public class VaultOrderVO implements Serializable {

    /**
     * 订单ID
     */
    @ApiModelProperty("订单ID")
    private Long id;

    /**
     * 区域ID
     */
    @ApiModelProperty(value = "部门ID", required = true)
    private Long departmentId;
    /**
     * 日期
     */
    private Long orderDate;

    /**
     * 订单类型（出库，入库）
     */
    @ApiModelProperty("订单类别")
    private Integer orderType;

    /**
     * 订单类型 0-ATM加钞 1-领缴款
     */
    @ApiModelProperty("出入库类型")
    private Integer subType;

    /**
     * 银行机构ID
     */
    @ApiModelProperty("银行机构ID")
    private Long bankId;

    /**
     * 列表数据
     */
    @ApiModelProperty("券别记录数据")
    List<VaultOrderRecordVO>  vaultRecordList;

    /**
     * ATM任务ID列表
     */
    @ApiModelProperty("任务ID集合，字符串")
    private List<String> taskIds;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String comments;

}
