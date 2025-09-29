package com.zcxd.voucher.db.model;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @author zccc
 */
@Data
@TableName("important_voucher_warehouse_record")
public class ImportantVoucherWarehouseRecord extends Model<ImportantVoucherWarehouseRecord> {
    private static final long serialVersionUID = 1L;
    /**
     * Id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 执行动作
     * 编辑
     * 审批
     * 反审
     * 删除
     */
    private String action;

    /**
     * 所属银行名称
     */
    private String bankName;

    /**
     * 出库去往网点
     */
    private String targetBankName;

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
     * 审核时间
     */
    private Long confirmTime;

    /**
     * 审批创建者
     */
    private String createName;

    /**
     * 审批者
     */
    private String updateName;

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
}
