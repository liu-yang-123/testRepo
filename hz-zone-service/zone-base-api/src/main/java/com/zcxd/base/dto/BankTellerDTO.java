package com.zcxd.base.dto;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;

/**
 * 
 * @ClassName BankTellerDTO
 * @Description 银行员工
 * @author 秦江南
 * @Date 2021年11月24日上午10:34:28
 */
@Data
public class BankTellerDTO {

    /**
     * 自增
     */
    private Long id;

    /**
     * 所属机构
     */
    private Long bankId;
    
    private String bankName;
    
    private Integer bankCategory;
    
    /**
     * 柜员编号
     */
    private String tellerNo;

    /**
     * 柜员姓名
     */
    private String tellerName;

    /**
     * 联系电话
     */
    private String mobile;

    /**
     * 管理员标志（0 - 一般柜员，2 - 管理员）
     */
    private Integer managerFlag;
    
    /**
     * 状态
     */
    private Integer statusT;
    
    /**
     * 员工id
     */
    private Long empId;
    
    /**
     * 备注
     */
    private String comments;

}
