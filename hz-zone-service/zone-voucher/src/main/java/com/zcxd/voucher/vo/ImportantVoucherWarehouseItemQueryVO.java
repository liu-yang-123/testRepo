package com.zcxd.voucher.vo;

import lombok.Data;

/**
 * @author zccc
 */
@Data
public class ImportantVoucherWarehouseItemQueryVO {
    private Integer id;
    /**
     * 所属银行名称
     */
    private String bankName;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 凭证类型
     */
    private String voucherType;

    /**
     * 出库去往网点
     */
    private String targetBankName;

    /**
     * 凭证名称
     */
    private String voucherName;

    /**
     * 是否审核通过
     *  1为已审核，0为未审核
     */
    private Short isConfirm;

    /**
     * 订单号
     */
    private String orderNumber;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 创建时间范围起始
     */
    private Long createTimeStart;

    /**
     * 创建时间范围结束
     */
    private Long createTimeEnd;

    /**
     * 所属部门
     */
    private Integer departmentId;

}
