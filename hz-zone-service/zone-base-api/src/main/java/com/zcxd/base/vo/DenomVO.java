package com.zcxd.base.vo;

import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName DenomVO
 * @Description 券别信息
 * @author 秦江南
 * @Date 2021年5月21日下午2:41:29
 */
@ApiModel("券别信息")
@Data
public class DenomVO {

    /**
     * 唯一标识
     */
	@ApiModelProperty(value = "用户Id",required = false)
    private Long id;

    /**
     * 货币代码
     */
    @ApiModelProperty(value = "货币代码",required = true)
    @NotBlank(message = "货币代码不能为空")
    @Size(max=32,message="货币代码最大长度为32")
    private String curCode;

    /**
     * 物理形态: C - 硬币，P - 纸币
     */
    @ApiModelProperty(value = "物理形态",required = true)
    @NotBlank(message = "物理形态不能为空")
    @Size(max=32,message="物理形态最大长度为32")
    private String attr;

    /**
     * 券别名称
     */
    @ApiModelProperty(value = "券别名称",required = true)
    @NotBlank(message = "券别名称不能为空")
    @Size(max=32,message="券别名称最大长度为32")
    private String name;

    /**
     * 面额值
     */
    @ApiModelProperty(value = "面额值",required = true)
    @NotNull(message = "面额值不能为空")
    private BigDecimal value;

    /**
     * 是否包含版别 0 - 不带版别，1 - 带版本
     */
    @ApiModelProperty(value = "是否包含版别",required = true)
    @NotNull(message = "是否包含版别不能为空")
    private Integer version;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序",required = false)
    private Integer sort;

    /**
     * 每把张数
     */
    @ApiModelProperty(value = "每把张数",required = true)
    @NotNull(message = "每把张数不能为空")
    private Integer wadSize;

    /**
     * 每捆张量/每盒枚数
     */
    @ApiModelProperty(value = "每捆张量",required = true)
    @NotNull(message = "每捆张量不能为空")
    private Integer bundleSize;

    /**
     * 每袋捆数/盒数
     */
    @ApiModelProperty(value = "每袋捆数",required = true)
    @NotNull(message = "每袋捆数不能为空")
    private Integer bagSize;

}
