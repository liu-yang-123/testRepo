package com.zcxd.pda.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * <p>
 * 分配钞盒列表
 * </p>
 *
 * @author admin
 * @since 2021-08-11
 */
@ToString
@Data
public class DispBoxDTO implements Serializable {

    /**
     * 钞盒编号
     */
    @ApiModelProperty(value = "钞盒编号")
    private String boxNo;
    /**
     * 使用情况
     */
    @ApiModelProperty(value = "钞盒使用标记(1 - 半盒，2 - 整盒)")
    private Integer useCount;

    /**
     * 使用情况
     */
    @ApiModelProperty(value = "面额，100，10")
    private Integer denomValue;
}
