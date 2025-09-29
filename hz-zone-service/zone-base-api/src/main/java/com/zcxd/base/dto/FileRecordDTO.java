package com.zcxd.base.dto;

import lombok.Data;

/**
 * 
 * @ClassName FileRecordDTO
 * @Description 文件传输记录
 * @author 秦江南
 * @Date 2022年11月14日下午3:14:05
 */
@Data
public class FileRecordDTO {

    private Long id;

    /**
     * 发送人
     */
    private String userName;

    /**
     * 单位名称
     */
    private String companyName;

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
     * 日期时间
     */
    private Long createTime;

}
