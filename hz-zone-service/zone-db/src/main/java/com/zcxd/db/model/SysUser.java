package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import lombok.Data;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
@Data
public class SysUser extends Model<SysUser> {

    private static final long serialVersionUID=1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

//    /**
//     * 所属部门
//     */
//    private Long departmentId;
//
//    private Long topDepartmentId;
    
    /**
     * 数据权限部门
     */
    private String authDepartments;
    
    /**
     * 银行id
     */
    private Long bankId;
    
    /**
     * 库存网点
     */
    private String stockBank;
    
    /**
     * 用户账号
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 用户姓名
     */
    private String nickName;

    /**
     * 角色列表
     */
    private String roleIds;

    /**
     * 状态
     */
    private Integer statusT;

    /**
     * 创建人
     */
    @TableField(fill= FieldFill.INSERT)
    private Long createUser;

    /**
     * 创建时间
     */
    @TableField(fill= FieldFill.INSERT)
    private Long createTime;

    /**
     * 更新人
     */
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Long updateUser;

    /**
     * 更新时间
     */
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Long updateTime;

    /**
     * 逻辑删除
     */
    private Integer deleted;

}
