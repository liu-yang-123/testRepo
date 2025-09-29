package com.zcxd.base.dto;

import lombok.Data;

/**
 * 
 * @ClassName LogDTO
 * @Description 日志信息
 * @author 秦江南
 * @Date 2021年5月12日下午7:00:48
 */
@Data
public class LogDTO {
	/**
     * 管理员
     */
    private String nickName;

    /**
     * 管理员地址
     */
    private String ip;

    /**
     * 操作分类
     */
    private Integer typeT;

    /**
     * 操作地址
     */
    private String action;

    /**
     * 操作内容
     */
    private String content;

    /**
     * 操作结果
     */
    private String result;

    /**
     * 创建时间
     */
    private Long createTime;
}
