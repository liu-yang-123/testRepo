package com.zcxd.base.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author songanwei
 * @date 2021-07-27
 */
@ApiModel("线路人员切换视图对象")
@Data
public class SchdResultChangeVO {

    @ApiModelProperty(value = "唯一标识",required = true)
    private Long id;


//    /**
//     * 车牌号码
//     */
//    @ApiModelProperty(value = "车牌号码",required = true)
//    @NotNull(message = "车牌号码不能为空")
//    private String vehicleNo;

    /**
     * 司机
     */
    @ApiModelProperty(value = "司机",required = true)
    private Long driver;

    /**
     * 护卫1
     */
    @ApiModelProperty(value = "护卫1",required = true)
    private Long scurityA;

    /**
     * 护卫2
     */
    @ApiModelProperty(value = "护卫2",required = true)
    private Long scurityB;

    /**
     * 钥匙员
     */
    @ApiModelProperty(value = "钥匙员",required = true)
    private Long keyMan;

    /**
     * 密码操作员
     */
    @ApiModelProperty(value = "密码操作员",required = true)
    private Long opMan;


    @ApiModelProperty(value = "车长",required = true)
    private Long leader;

}
