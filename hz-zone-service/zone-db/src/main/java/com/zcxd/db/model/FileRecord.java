package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import lombok.Data;

/**
 * <p>
 * 文件传输记录
 * </p>
 *
 * @author admin
 * @since 2022-11-08
 */
@Data
public class FileRecord extends Model<FileRecord> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 发送人
     */
    private Long userId;
    
    /**
     * 发送单位
     */
    private Long sendCompanyId;

    /**
     * 接收单位
     */
    private Long companyId;

    /**
     * 标题
     */
    private String recordTitle;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件大小
     */
    private Long fileSize;
    
    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件地址
     */
    private String filePath;

    /**
     * 是否已读
     */
    private Integer readSign;

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
