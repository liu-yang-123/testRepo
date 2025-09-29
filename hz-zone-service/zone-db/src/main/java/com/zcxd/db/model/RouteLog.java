package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 车长日志
 * </p>
 *
 * @author admin
 * @since 2021-10-11
 */
@ToString
@Data
public class RouteLog extends Model<RouteLog> {

    private static final long serialVersionUID=1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 线路（选填）
     */
    private Long routeId;

    /**
     * 日志详情
     */
    private String detail;

    /**
     * 备注
     */
    private String comments;

    /**
     * 总体日志结果（所有子项正常时填 1 - 存在异常填- 0）
     */
    private Integer result;

    /**
     * 车长
     */
    private Long leader;

    /**
     * 创建时间
     */
    @TableField(fill= FieldFill.INSERT)
    private Long createTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
