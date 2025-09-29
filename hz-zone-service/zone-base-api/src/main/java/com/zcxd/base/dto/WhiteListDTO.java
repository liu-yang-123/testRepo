package com.zcxd.base.dto;

import lombok.Data;

/**
 * 
 * @ClassName WhiteListDTO
 * @Description 白名单信息
 * @author 秦江南
 * @Date 2021年5月13日下午3:00:43
 */
@Data
public class WhiteListDTO {
	/**
     * 唯一标识
     */
    private Long id;

    /**
     * 白名单ip备注
     */
    private String ipRemarks;

    /**
     * 白名单IP
     */
    private String ipAddress;

    /**
     * MAC地址
     */
    private String macAddress;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

}
