package com.zcxd.base.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 箱包视图对象
 * @author songanwei
 * @date 2021-11-22
 */
@ApiModel("箱包视图对象")
@Data
public class BoxpackVO implements Serializable {

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    private Long id;

    /**
     * 部门ID
     */
    @ApiModelProperty(value = "部门ID", required = true)
    private Long departmentId;

    /**
     * 箱包编号
     */
    @ApiModelProperty(value = "箱包编号", required = true)
    private String boxNo;

    /**
     * 箱包名称
     */
    @ApiModelProperty(value = "箱包名称", required = true)
    private String boxName;

    /**
     * 机构ID
     */
    @ApiModelProperty(value = "机构ID", required = true)
    private Long bankId;

    /**
     * 公用机构
     */
    @ApiModelProperty(value = "公用机构", required = true)
    private Long shareBankId;

    /**
     * rfid编号
     */
    @ApiModelProperty(value = "RFID编号", required = true)
    private String rfid;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String comments;

}
