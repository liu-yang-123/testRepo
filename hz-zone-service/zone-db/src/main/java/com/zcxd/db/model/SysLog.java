package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 操作日志表
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
@Data
public class SysLog extends Model<SysLog> {

    private static final long serialVersionUID=1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 管理员
     */
    private Long userId;

    /**
     * 管理员地址
     */
    private String ip;

    /**
     * 操作分类
     */
    private Integer typeT;

    /**
     * 操作地址
     */
    private String action;

    /**
     * 操作内容
     */
    private String content;

    /**
     * 操作结果
     */
    private String result;

    /**
     * 创建时间
     */
    private Long createTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
