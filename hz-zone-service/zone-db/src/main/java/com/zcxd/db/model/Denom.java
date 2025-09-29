package com.zcxd.db.model;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 券别信息
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public class Denom extends Model<Denom> {

    private static final long serialVersionUID=1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 货币代码
     */
    private String curCode;

    /**
     * 物理形态: C - 硬币，P - 纸币
     */
    private String attr;

    /**
     * 券别名称
     */
    private String name;

    /**
     * 面额值
     */
    private BigDecimal value;

    /**
     * 是否包含版别 0 - 不带版别，1 - 带版本
     */
    private Integer version;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 每把张数
     */
    private Integer wadSize;

    /**
     * 每捆张量/每盒枚数
     */
    private Integer bundleSize;

    /**
     * 每袋捆数/盒数
     */
    private Integer bagSize;

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

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getWadSize() {
        return wadSize;
    }

    public void setWadSize(Integer wadSize) {
        this.wadSize = wadSize;
    }

    public Integer getBundleSize() {
        return bundleSize;
    }

    public void setBundleSize(Integer bundleSize) {
        this.bundleSize = bundleSize;
    }

    public Integer getBagSize() {
        return bagSize;
    }

    public void setBagSize(Integer bagSize) {
        this.bagSize = bagSize;
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
        return "Denom{" +
        "id=" + id +
        ", curCode=" + curCode +
        ", attr=" + attr +
        ", name=" + name +
        ", value=" + value +
        ", version=" + version +
        ", sort=" + sort +
        ", wadSize=" + wadSize +
        ", bundleSize=" + bundleSize +
        ", bagSize=" + bagSize +
        ", createUser=" + createUser +
        ", createTime=" + createTime +
        ", updateUser=" + updateUser +
        ", updateTime=" + updateTime +
        ", deleted=" + deleted +
        "}";
    }

    public int toWad(BigDecimal amount) {
        try {
            int pieces = amount.divide(this.value).intValue();
            return pieces/this.wadSize;
        } catch (ArithmeticException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public int toBundle(BigDecimal amount) {
        try {
            int pieces = amount.divide(this.value,BigDecimal.ROUND_DOWN).intValue();
            return pieces/(this.wadSize*this.bundleSize);
        } catch (ArithmeticException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public float toBundleFloat(BigDecimal amount) {
        try {
            int pieces = amount.divide(this.value,BigDecimal.ROUND_DOWN).intValue();
            return (float)pieces/(this.wadSize*this.bundleSize);
        } catch (ArithmeticException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public int toBag(BigDecimal amount) {
        try {
            int pieces = amount.divide(this.value,BigDecimal.ROUND_DOWN).intValue();
            return pieces/(this.wadSize*this.bundleSize*this.bagSize);
        } catch (ArithmeticException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public long toPieces(BigDecimal amount) {
        try {
            return amount.divide(this.value,BigDecimal.ROUND_DOWN).longValue();
        } catch (ArithmeticException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
