package com.zcxd.base.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
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
public class BatchIdsVO implements Serializable {
    @ApiModelProperty("记录id列表")
    @NotNull(message = "记录id不能为空")
    private List<Long> ids;
}
