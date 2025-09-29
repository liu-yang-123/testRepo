package com.zcxd.base.dto;

import lombok.Data;

/**
 * 
 * @ClassName CheckItemResultDTO
 * @Description 巡检检查结果DTO
 * @author 秦江南
 * @Date 2021年6月29日下午2:18:04
 */
@Data
public class CheckItemResultDTO {
	private Integer cardReader;
	private Integer cashOutlet;
	private Integer keypadMask;
	private Integer operationTips;
	private Integer thingInstall;
	private Integer thingStick;
}
