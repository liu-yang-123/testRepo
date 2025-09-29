package com.zcxd.base.dto;

import lombok.Data;

/**
 * 
 * @ClassName RoleDTO
 * @Description 角色信息
 * @author 秦江南
 * @Date 2021年5月13日下午5:01:40
 */
@Data
public class RoleDTO {
	/**
     * 唯一标识
     */
    private Long id;

    /**
     * 角色名
     */
    private String roleName;

    /**
     * 角色描述
     */
    private String describes;
}
