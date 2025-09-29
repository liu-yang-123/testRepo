package com.zcxd.pda.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 批量idVO
 * </p>
 *
 * @author admin
 * @since 2021-08-11
 */
@ToString
@Data
public class BatchBoxNosVO implements Serializable {

//    @ApiModelProperty("操作类型：0 - 原装钞盒重新绑定，1 - 已拆封钞盒解绑")
//    private Integer opType;

    /**
     * 清分人
     */
    @ApiModelProperty(value = "交接清点人")
    private Long clearMan;
    /**
     * 复核人
     */
    @ApiModelProperty(value = "交接复核人")
    private Long checkMan;

    @ApiModelProperty("钞盒列表")
    @NotNull(message = "钞盒列表不能为空")
    private List<String> boxNos;
}
