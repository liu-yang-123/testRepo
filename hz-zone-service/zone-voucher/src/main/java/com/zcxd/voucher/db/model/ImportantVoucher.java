package com.zcxd.voucher.db.model;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.ToString;

/**
 * @author zccc
 */
@Data
@ToString
public class ImportantVoucher extends Model<ImportantVoucher> {
    private static final long serialVersionUID = 1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属银行Id
     */
    private Long bankId;

    /**
     * 重空名称
     */
    private String name;

    /**
     * 重空类型
     *  1：卡类
     *  2：贵金属
     *  3：其他凭证类
     */
    private Short type;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createTime;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;

    /**
     * 所属部门
     */
    private Integer departmentId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标志
     */
    @TableLogic
    private Integer deleted;
}
