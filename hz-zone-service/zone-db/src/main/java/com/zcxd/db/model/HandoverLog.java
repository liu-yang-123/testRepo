package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import lombok.Data;

/**
 * <p>
 * 
 * </p>
 *
 * @author admin
 * @since 2022-05-13
 */
@Data
public class HandoverLog extends Model<HandoverLog> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 事业部ID
     */
    private Long departmentId;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String contents;

    /**
     * 图片
     */
    private String imageUrl;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 附件文件
     */
    private String fileUrl;

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
    private Integer deleted;

}
