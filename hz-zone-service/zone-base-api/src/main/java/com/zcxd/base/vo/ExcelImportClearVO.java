package com.zcxd.base.vo;

import lombok.Data;

/**
 * 
 * @ClassName ExcelImportClearVO
 * @Description 导入清分任务Excel解析
 * @author 秦江南
 * @Date 2021年7月9日下午3:52:09
 */
@Data
public class ExcelImportClearVO {
		//Sheet数量
		int sheetNum;
		//数据Sheet开始位置
		int sheetStartNum;
		//银行名称
//		String bankName;
		//线路编号位置
		int routeNoCell;
		//线路日期位置
		int routeDateCell;
		//序号位置
		int serialCell;
		//设备编号位置
		int terNoCell;
		//库存金额位置
		int amountCell;
		//库存金额位置
		int commentsCell;
		
		public ExcelImportClearVO(int sheetNum, int sheetStartNum, int routeNoCell, int routeDateCell,
				int serialCell, int terNoCell, int amountCell, int commentsCell) {
			super();
			this.sheetNum = sheetNum;
			this.sheetStartNum = sheetStartNum;
			this.routeNoCell = routeNoCell;
			this.routeDateCell = routeDateCell;
			this.serialCell = serialCell;
			this.terNoCell = terNoCell;
			this.amountCell = amountCell;
			this.commentsCell = commentsCell;
		}
		

		
}
