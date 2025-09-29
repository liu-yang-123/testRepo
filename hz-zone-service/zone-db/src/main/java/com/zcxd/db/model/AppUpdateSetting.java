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
 * app升级设置管理
 * </p>
 *
 * @author admin
 * @since 2021-09-13
 */
@ToString
@Data
public class AppUpdateSetting extends Model<AppUpdateSetting> {

    private static final long serialVersionUID=1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * app类型
     */
    private Integer appType;

    /**
     * app升级时间
     */
    private Long appUpdateTime;

    /**
     * 版本名称
     */
    private String versionName;

    /**
     * 版本号
     */
    private Integer versionNumber;

    /**
     * 描述
     */
    private String description;

    /**
     * atm设备（选填）
     */
    private Integer forceUpdate;

    /**
     * 银行网点
     */
    private String packageSize;

    /**
     * 关联清机任务id
     */
    private String packageUrl;


    private Integer statusT;

    /**
     * 创建人
     */
    private Long createUser;

    /**
     * 创建时间
     */
    @TableField(fill= FieldFill.INSERT)
    private Long createTime;

}
