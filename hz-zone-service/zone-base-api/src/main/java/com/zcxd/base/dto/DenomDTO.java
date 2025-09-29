package com.zcxd.base.dto;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 
 * @ClassName DenomVO
 * @Description 券别信息
 * @author 秦江南
 * @Date 2021年5月21日下午2:41:29
 */
@Data
public class DenomDTO {

    /**
     * 唯一标识
     */
    private Long id;

    /**
     * 货币代码
     */
    private String curCode;

    /**
     * 物理形态: C - 硬币，P - 纸币
     */
    private String attr;

    /**
     * 券别名称
     */
    private String name;

    /**
     * 面额值
     */
    private BigDecimal value;

    /**
     * 是否包含版别 0 - 不带版别，1 - 带版本
     */
    private Integer version;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 每把张数
     */
    private Integer wadSize;

    /**
     * 每捆张量/每盒枚数
     */
    private Integer bundleSize;

    /**
     * 每袋捆数/盒数
     */
    private Integer bagSize;

}
