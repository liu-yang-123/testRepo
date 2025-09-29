package com.zcxd.gun.db.model;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.ToString;

/**
 * @author zccc
 */
@ToString
@Data
public class Gun extends Model<Gun> {
    private static final long serialVersionUID=1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 枪号/弹盒号
     */
    private String gunCode;

    /**
     * 持枪证Id
     */
    private Long gunLicenceId;

    /**
     * 内部编号
     */
    private Long internalNum;

    /**
     * 配发日期
     * 2019-01-24
     */
    private String buyDate;

    /**
     * 枪械类型，枪盒默认为0
     */
    private Long gunCategory;

    /**
     * 1：库存
     * 2：已发出
     * 3：禁用
     */
    private Integer gunStatus;

    /**
     * 所属顶级部门
     */
    private Integer departmentId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 出勤次数
     */
    private Integer userCount;

    /**
     * 擦拭标志位
     * 0：未擦拭
     * 1：已擦拭
     */
    private Integer isClean;

    /**
     * 分解标志位
     * 0：未分解
     * 1：已分解
     */
    private Integer isCheck;

    /**
     * 擦拭日期
     */
    private Long cleanDate;

    /**
     * 分解日期
     */
    private Long checkDate;

    /**
     * 操作员
     */
    private String operatorName;

    /**
     * 创建人
     */
    @TableField(fill= FieldFill.INSERT)
    private Long createUser;

    /**
     * 创建时间
     */
    @TableField(fill= FieldFill.INSERT)
    private Long createTime;

    /**
     * 更新人
     */
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Long updateUser;

    /**
     * 更新时间
     */
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Long updateTime;

    /**
     * 删除标志
     */
    @TableLogic
    private Integer deleted;

}
