package com.zcxd.base.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author songanwei
 * @date 2021-06-15
 */
@ApiModel("ATM清分创建对象")
@Data
public class ClearTaskCreateVO implements Serializable {

    /**
     * 所属部门ID
     */
    @ApiModelProperty("部门ID")
    private Long departmentId;

    /**
     * 线路ID
     */
    @ApiModelProperty("线路ID")
    private Long routeId;

    /**
     * atm清分列表
     */
    @ApiModelProperty("Atm设备数据列表")
    private List<ClearAtmVO> atmList;

}
