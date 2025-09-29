package com.zcxd.gun.vo;

import lombok.Data;

/**
 * @author songanwei
 * @date 2021-07-27
 */
@Data
public class SchdResultChangeVO {

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
    private Long driver;

    /**
     * 护卫1
     */
    private Long scurityA;

    /**
     * 护卫2
     */
    private Long scurityB;

    /**
     * 钥匙员
     */
    private Long keyMan;

    /**
     * 密码操作员
     */
    private Long opMan;

    private Long leader;

}
