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
public class ImportantVoucherNumberSegment extends Model<ImportantVoucherNumberSegment> {
    private static final long serialVersionUID = 1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 重空凭证Id
     */
    private Long voucherId;

    /**
     * 重空类型  （用于查询）
     *  1：卡类
     *  2：贵金属
     *  3：其他凭证类
     */
    private Short type;

    /**
     * 重空名称
     */
    private String name;

    /**
     * 起始号段
     */
    private Long numberStart;

    /**
     * 结束号段
     */
    private Long numberEnd;

    /**
     * 当前号段数量
     */
    private Long count;

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

    public ImportantVoucherNumberSegment(){}

    public boolean countSegmentNum() {
        if (this.numberStart == null || this.numberEnd == null
                || this.numberStart > this.numberEnd) {
            return false;
        }
        this.count = this.numberEnd - this.numberStart + 1;
        return true;
    }
}
