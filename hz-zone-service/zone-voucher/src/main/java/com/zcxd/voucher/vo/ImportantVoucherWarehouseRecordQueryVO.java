package com.zcxd.voucher.vo;

import lombok.Data;

/**
 * @author zccc
 */
@Data
public class ImportantVoucherWarehouseRecordQueryVO {
    private Long id;
    /**
     * 所属银行名称
     */
    private String bankName;

    /**
     * 出库去往网点
     */
    private String targetBankName;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 凭证类型
     */
    private String voucherType;

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
     * 审核时间
     */
    private Long confirmTime;

    /**
     * 审核时间开始
     */
    private Long confirmTimeStart;

    /**
     * 审核时间结束
     */
    private Long confirmTimeEnd;

    /**
     * 部门Id
     */
    private Integer departmentId;

}
