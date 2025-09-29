package com.zcxd.base.dto;

import lombok.Data;

/**
 * 
 * @ClassName UserDTO
 * @Description 用户信息
 * @author 秦江南
 * @Date 2021年5月12日上午9:45:33
 */
@Data
public class UserDTO {
    /**
     * 唯一标识
     */
    private Long id;

//    /**
//     * 所属部门Id
//     */
//    private Long departmentId;
    
    /**
     * 角色列表
     */
    private String authDepartments;
    
//    /**
//     * 所属部门
//     */
//    private String departmentName;

    /**
     * 所属银行
     */
    private Long bankId;
    
    /**
     * 库存网点
     */
    private String stockBank;
    
    /**
     * 库存网点名称
     */
    private String stockBankName;
    
    /**
     * 所属银行名称
     */
    private String bankName;
    
    /**
     * 用户账号
     */
    private String username;

    /**
     * 用户姓名
     */
    private String nickName;

    /**
     * 角色列表
     */
    private String roleIds;
}
