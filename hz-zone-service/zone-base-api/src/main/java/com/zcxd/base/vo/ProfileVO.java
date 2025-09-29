package com.zcxd.base.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author songanwei
 * @date 2021/5/12 11:46
 */
@ApiModel("登录模型")
@Data
public class ProfileVO implements Serializable {

    /**
     * 密码字段
     */
    @ApiModelProperty(value = "新密码字段",required = true)
    @Size(min = 6,message = "密码长度不能少于6位数")
    @NotNull(message = "密码不能为空")
    private String password;

    /**
     * 原密码
     */
    @ApiModelProperty(value = "原密码字段",required = true)
    @Size(min = 6,message = "原密码长度不能少于6位数")
    @NotNull(message = "原密码不能为空")
    private String oldPassword;
}
