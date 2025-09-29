package com.zcxd.sync.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * 上传日志表
 * @Author: lilanglang
 * @Date: 2021/9/2 11:06
 **/
@TableName("sys_upload_log")
public class UploadLog extends Model<UploadLog> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 数据日期
     */
    private String dataDate;

    private Integer dataType;
    /**
     * 上传文件路径
     */
    private String fileUrl;

    //内部文件名
    private String fileName;
    /**
     * 文件唯一标识
     */
    private String uuid;
    /**
     * 文件记录条数
     */
    private Integer count;
    /**
     * 上传结果：0 - 失败，1-成功
     */
    private Integer statusT;

    /**
     * 上传时间
     */
    private Long uploadTime;
    /**
     * 是否密码保护
     */
    private Integer protect;
    /**
     * 备注
     */
    private String comments;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDataDate() {
        return dataDate;
    }

    public void setDataDate(String dataDate) {
        this.dataDate = dataDate;
    }

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getStatusT() {
        return statusT;
    }

    public void setStatusT(Integer status) {
        this.statusT = status;
    }

    public Long getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Long uploadTime) {
        this.uploadTime = uploadTime;
    }

    public Integer getProtect() {
        return protect;
    }

    public void setProtect(Integer protect) {
        this.protect = protect;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "UploadLog{" +
                "id=" + id +
                ", dataDate='" + dataDate + '\'' +
                ", dataType=" + dataType +
                ", fileUrl='" + fileUrl + '\'' +
                ", count=" + count +
                ", statusT=" + statusT +
                ", uploadTime=" + uploadTime +
                '}';
    }
}
