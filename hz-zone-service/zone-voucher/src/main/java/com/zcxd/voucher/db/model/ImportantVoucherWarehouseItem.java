package com.zcxd.voucher.db.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @author zccc
 */
@Data
@TableName("important_voucher_warehouse_item")
public class ImportantVoucherWarehouseItem {
    private static final long serialVersionUID = 1L;
    /**
     * Id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属银行名称
     */
    private String bankName;

    /**
     * 银行Id
     */
    private Long voucherId;

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
     * 凭证起始
     */
    private Long start;

    /**
     * 凭证结束
     */
    private Long end;

    /**
     * 凭证数量
     */
    private Long count;

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
     * 审核人
     */
    private String confirmName;

    /**
     * 审核时间
     */
    private Long confirmTime;

    /**
     * 反审核人
     */
    private String cancelName;

    /**
     * 反审核时间
     */
    private Long cancelTime;

    /**
     * 创建者
     */
    private String createName;

    /**
     * 任务Id
     */
    private Long taskId;

    /**
     * 任务时间
     */
    private Long taskDate;

    /**
     * 任务路线
     */
    private String taskLine;

    /**
     * 任务箱包
     */
    private String taskCashbox;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人
     */
    @JsonIgnore
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    /**
     * 创建时间
     */
    @JsonIgnore
    @TableField(fill = FieldFill.INSERT)
    private Long createTime;

    /**
     * 更新人
     */
    @JsonIgnore
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

    /**
     * 更新时间
     */
    @JsonIgnore
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;

    /**
     * 所属部门
     */

    private Integer departmentId;

    /**
     * 删除标志
     */
    @TableLogic
    private Integer deleted;

    public boolean countSegmentNum() {
        if (this.start == null || this.end == null
                || this.start > this.end) {
            return false;
        }
        this.count = this.end - this.start + 1;
        return true;
    }
}
