package com.zcxd.base.dto;

import lombok.Data;

/**
 * 
 * @ClassName FileCompanyDTO
 * @Description 单位信息
 * @author 秦江南
 * @Date 2022年11月8日下午3:53:40
 */
@Data
public class FileCompanyDTO {

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
    private String operator;
    
    /**
     * 操作员id
     */
    private String userIds;
}
