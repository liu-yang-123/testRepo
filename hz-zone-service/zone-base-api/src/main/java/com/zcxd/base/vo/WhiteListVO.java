package com.zcxd.base.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author songanwei
 * @date 2021-10-20
 */
@ApiModel("白名单视图对象")
@Data
public class WhiteListVO implements Serializable {

    /**
     * ID
     */
    @ApiModelProperty("系统ID")
    private Long id;

    /**
     * ip地址
     */
    @ApiModelProperty("ip地址")
    @Size(max=32,message="ipAddress最大长度为32")
    private String ipAddress;

    /**
     * mac地址
     */
    @ApiModelProperty("mac地址")
    @Size(max=32,message="macAddress最大长度为32")
    private String macAddress;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    @Size(max=32,message="ipRemarks最大长度为32")
    private String ipRemarks;

}
