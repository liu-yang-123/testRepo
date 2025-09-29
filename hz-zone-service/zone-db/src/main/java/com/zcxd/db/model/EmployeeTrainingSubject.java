package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 培训主题
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public class EmployeeTrainingSubject extends Model<EmployeeTrainingSubject> {

    private static final long serialVersionUID=1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 培训日期
     */
    private Long trainDate;

    /**
     * 培训类别
     */
    private String trainType;

    /**
     * 培训主题
     */
    private String trainTitle;

    /**
     * 培训内容
     */
    private String trainContent;

    /**
     * 培训老师
     */
    private String trainer;

    /**
     * 培训地点
     */
    private String place;

    /**
     * 培训时长
     */
    private Integer times;

    /**
     * 考核方式
     */
    private String test;

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

    public Long getTrainDate() {
        return trainDate;
    }

    public void setTrainDate(Long trainDate) {
        this.trainDate = trainDate;
    }

    public String getTrainType() {
        return trainType;
    }

    public void setTrainType(String trainType) {
        this.trainType = trainType;
    }

    public String getTrainTitle() {
        return trainTitle;
    }

    public void setTrainTitle(String trainTitle) {
        this.trainTitle = trainTitle;
    }

    public String getTrainContent() {
        return trainContent;
    }

    public void setTrainContent(String trainContent) {
        this.trainContent = trainContent;
    }

    public String getTrainer() {
        return trainer;
    }

    public void setTrainer(String trainer) {
        this.trainer = trainer;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
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
        return "EmployeeTrainingSubject{" +
        "id=" + id +
        ", trainDate=" + trainDate +
        ", trainType=" + trainType +
        ", trainTitle=" + trainTitle +
        ", trainContent=" + trainContent +
        ", trainer=" + trainer +
        ", place=" + place +
        ", times=" + times +
        ", test=" + test +
        ", createUser=" + createUser +
        ", createTime=" + createTime +
        ", updateUser=" + updateUser +
        ", updateTime=" + updateTime +
        ", deleted=" + deleted +
        "}";
    }
}
