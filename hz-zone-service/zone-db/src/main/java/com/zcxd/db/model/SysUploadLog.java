package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 数据同步日志表
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public class SysUploadLog extends Model<SysUploadLog> {

    private static final long serialVersionUID=1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 数据日期
     */
    private String dataDate;

    /**
     * 数据表类型
     */
    private Integer dataType;

    /**
     * 文件名
     */
    private String fileUrl;

    /**
     * 内部文件名称
     */
    private String fileName;

    /**
     * 文件唯一序列号
     */
    private String uuid;

    /**
     * 记录个数
     */
    private Integer count;

    /**
     * 上传结果：0 - 失败，1-成功
     */
    private Integer statusT;

    /**
     * 创建时间
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


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public void setStatusT(Integer statusT) {
        this.statusT = statusT;
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
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "SysUploadLog{" +
        "id=" + id +
        ", dataDate=" + dataDate +
        ", dataType=" + dataType +
        ", fileUrl=" + fileUrl +
        ", fileName=" + fileName +
        ", uuid=" + uuid +
        ", count=" + count +
        ", statusT=" + statusT +
        ", uploadTime=" + uploadTime +
        ", protect=" + protect +
        ", comments=" + comments +
        "}";
    }
}
