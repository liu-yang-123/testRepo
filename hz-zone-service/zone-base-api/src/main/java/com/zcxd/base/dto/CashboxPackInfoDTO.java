package com.zcxd.base.dto;

import java.util.List;

import com.zcxd.db.model.CashboxScanRecord;

import lombok.Data;

@Data
public class CashboxPackInfoDTO {
	//分配线路
	private String routeNo;
	//第一次加钞设备编号
	private String atmTerNo;
	//第二次加钞设备编号
	private String secondAtmTerNo;
	//扫码记录
	private List<CashboxScanRecordDTO> scans;
}
