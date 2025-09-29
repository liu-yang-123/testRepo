package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import lombok.Data;

/**
 * <p>
 * 单位管理
 * </p>
 *
 * @author admin
 * @since 2022-11-08
 */
@Data
public class FileCompany extends Model<FileCompany> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 单位号
     */
    private String companyNo;

    /**
     * 单位名称
     */
    private String companyName;

    /**
     * 单位类别
     */
    private Integer companyType;

    /**
     * 单位状态
     */
    private Integer statusT;

    /**
     * 文件目录
     */
    private String fileDirectory;
    
    /**
     * 操作员
     */
    private String userIds;
    
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
