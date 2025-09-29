package com.zcxd.pda.dto;

import lombok.Data;

@Data
public class LoginDTO {
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * token值
     */
    private  String token;
    /**
     * 用户名
     */
    private  String userName;
    /**
     * 用户账号
     */
    private  String userAccount;
    /**
     * 角色类型   0-未知 1-库管员 2-清分员 3-清机员 4-银行柜员 5-系统管理员  6-银行管理员
     */
    private  Integer  roleType;
    /**
     * 修改密码标志
     */
    private  Integer  changePwd;
}
