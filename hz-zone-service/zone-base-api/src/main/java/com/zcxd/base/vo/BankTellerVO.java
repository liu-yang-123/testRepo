package com.zcxd.base.vo;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName BankTellerVO
 * @Description 早送晚收员工
 * @author 秦江南
 * @Date 2021年11月23日下午2:39:49
 */
@Data
public class BankTellerVO {
    /**
     * 自增
     */
	@ApiModelProperty(value = "用户Id",required = false)
    private Long id;

    /**
     * 所属机构
     */
	@ApiModelProperty(value = "所属机构",required = true)
	@NotNull(message = "所属机构不能为空")
    private Long bankId;
    
    /**
     * 部门（事业部id）
     */
	@ApiModelProperty(value = "所属部门",required = true)
	@NotNull(message = "所属部门不能为空")
    private Long departmentId;

    /**
     * 柜员编号
     */
    @ApiModelProperty(value = "柜员编号",required = false)
    private String tellerNo;

    /**
     * 柜员姓名
     */
    @ApiModelProperty(value = "柜员姓名",required = false)
    private String tellerName;

    /**
     * 联系电话
     */
    @ApiModelProperty(value = "联系电话",required = false)
    private String mobile;

    /**
     * 管理员标志（0 - 一般柜员，2 - 管理员）
     */
    @ApiModelProperty(value = "管理员标志",required = false)
    private Integer managerFlag;
    
    /**
     * 员工id
     */
    @ApiModelProperty(value = "员工id",required = false)
    private Long empId;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注",required = false)
    private String comments;

}
