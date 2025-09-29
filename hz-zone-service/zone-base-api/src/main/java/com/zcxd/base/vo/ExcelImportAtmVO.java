package com.zcxd.base.vo;

import lombok.Data;

/**
 * 
 * @ClassName ExcelImportAtmVO
 * @Description 导入ATM清机任务Excel解析
 * @author 秦江南
 * @Date 2021年7月7日下午2:26:05
 */
@Data
public class ExcelImportAtmVO {
	//Sheet数量
	int sheetNum;
	//数据Sheet开始位置
	int sheetStartNum;
	//银行名称
//	String bankName;
	//线路编号位置
	int routeNoCell;
	//线路编号前字符
	String routeNoTop;
	//线路编号后字符
	String routeNoBack;
	//线路日期位置
	int routeDateCell;
	//序号位置
	int serialCell;
	//设备编号位置
	int terNoCell;
	//加款金额位置
	int amountCell;
	//备注位置
	int commonCell;
	
	public ExcelImportAtmVO(int sheetNum, int sheetStartNum, int routeNoCell, String routeNoTop, String routeNoBack,
			int routeDateCell, int serialCell, int terNoCell, int amountCell, int commonCell) {
		super();
		this.sheetNum = sheetNum;
		this.sheetStartNum = sheetStartNum;
		this.routeNoCell = routeNoCell;
		this.routeNoTop = routeNoTop;
		this.routeNoBack = routeNoBack;
		this.routeDateCell = routeDateCell;
		this.serialCell = serialCell;
		this.terNoCell = terNoCell;
		this.amountCell = amountCell;
		this.commonCell = commonCell;
	}
	
	
}
