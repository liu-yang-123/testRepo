package com.zcxd.pda.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author songanwei
 * @date 2021/4/19 14:17
 */
@ApiModel("用户登录")
@Data
public class LoginAccountVO implements Serializable {

    /**
     * 登录账号
     */
    @ApiModelProperty(value = "用户名",required = true)
    @NotBlank(message = "用户名不能为空")
    @Size(max=32,message="用户名最大长度为32")
    private String userName;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码",required = true)
    @NotBlank(message = "密码不能为空")
    @Size(max=32,message="密码最大长度为32")
    private String password;

    /**
     * pda终端编号
     */
    @ApiModelProperty(value = "终端sn",required = true)
    @NotBlank(message = "终端sn不能为空")
    @Size(max=16,message="密码最大长度为16")
    private String tersn;

}
