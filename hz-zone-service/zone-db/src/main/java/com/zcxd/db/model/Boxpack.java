package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 尾箱信息
 * @author admin
 * @since 2021-04-27
 */
@Data
public class Boxpack extends Model<Boxpack> {

    private static final long serialVersionUID=1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 部门ID
     */
    private Long departmentId;

    /**
     * 箱包编号
     */
    private String boxNo;

    /**
     * 箱包名称
     */
    private String boxName;

    /**
     * 箱包类型
     */
    private Integer boxType;

    /**
     * 所属网点
     */
    private Long bankId;

    /**
     * 公用机构
     */
    private Long shareBankId;

    /**
     * RFID卡号
     */
    private String rfid;

    /**
     * 备注
     */
    private String comments;

    /**
     * 箱包状态：1-网点 2-途中 3-库房
     */
    private Integer statusT;

    /**
     * 创建人
     */
    private Long createUser;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新人
     */
    private Long updateUser;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 删除标志
     */
    private Integer deleted;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
