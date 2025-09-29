package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 币种信息
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public class Currency extends Model<Currency> {

    private static final long serialVersionUID=1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 货币编码，三位字符：CNY,USD
     */
    private String curCode;

    /**
     * 货币名称
     */
    private String curName;

    /**
     * 流通地区
     */
    private String curCountry;

    /**
     * 符号
     */
    private String curCharacter;

    /**
     * 排序
     */
    private Integer curIndex;

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


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurCode() {
        return curCode;
    }

    public void setCurCode(String curCode) {
        this.curCode = curCode;
    }

    public String getCurName() {
        return curName;
    }

    public void setCurName(String curName) {
        this.curName = curName;
    }

    public String getCurCountry() {
        return curCountry;
    }

    public void setCurCountry(String curCountry) {
        this.curCountry = curCountry;
    }

    public String getCurCharacter() {
        return curCharacter;
    }

    public void setCurCharacter(String curCharacter) {
        this.curCharacter = curCharacter;
    }

    public Integer getCurIndex() {
        return curIndex;
    }

    public void setCurIndex(Integer curIndex) {
        this.curIndex = curIndex;
    }

    public Long getCreateUser() {
        return createUser;
    }

    public void setCreateUser(Long createUser) {
        this.createUser = createUser;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(Long updateUser) {
        this.updateUser = updateUser;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "Currency{" +
        "id=" + id +
        ", curCode=" + curCode +
        ", curName=" + curName +
        ", curCountry=" + curCountry +
        ", curCharacter=" + curCharacter +
        ", curIndex=" + curIndex +
        ", createUser=" + createUser +
        ", createTime=" + createTime +
        ", updateUser=" + updateUser +
        ", updateTime=" + updateTime +
        ", deleted=" + deleted +
        "}";
    }
}
