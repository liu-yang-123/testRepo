package com.zcxd.base.dto;

import lombok.Data;

/**
 * 
 * @ClassName MenuDTO
 * @Description 菜单信息
 * @author 秦江南
 * @Date 2021年5月11日上午8:56:05
 */
@Data
public class MenuDTO {
	/**
     * 唯一标识
     */
    private Long id;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 父级菜单
     */
    private Long pid;

    /**
     * 地址
     */
    private String url;

    /**
     * 排序
     */
    private Integer sort;
}
