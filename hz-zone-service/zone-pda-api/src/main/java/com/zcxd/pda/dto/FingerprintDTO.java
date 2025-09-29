package com.zcxd.pda.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author shijin
 * @date 2021/5/18 10:17
 */
@ApiModel("指纹数据")
@Data
public class FingerprintDTO implements Serializable {
    /**
     * id
     */
    @ApiModelProperty(value = "指纹id")
    private Long id;
    /**
     * id
     */
    @ApiModelProperty(value = "用户id",required = true)
    @NotNull(message = "用户Id不能为空")
    private Long userId;

    /**
     * 人员姓名
     */
    @ApiModelProperty(value = "用户姓名")
    private String userName;
    /**
     * 用户类型
     */
    @ApiModelProperty(value = "用户类别（0 - 员工，1 - 银行柜员 ）",required = true)
    @NotNull(message = "用户类型不能为空")
    private Integer userType;

    @ApiModelProperty(value = "主管标志（0 - 员工，1 - 主管）")
    private Integer master;

    /**
     * 指纹序号
     */
    @ApiModelProperty(value = "手指序号（从左到右 0 - 9",required = true)
    @NotNull(message = "手指序号不能为空")
    private Integer fingerIdx;

    /**
     * 指纹特征
     */
    @ApiModelProperty(value = "指纹特征",required = true)
    @NotBlank(message = "指纹特征不能为空")
    private String fingerPrint;

}
