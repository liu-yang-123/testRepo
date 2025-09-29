package com.zcxd.base.dto.excel;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;

import lombok.Data;

@Data
@ContentRowHeight(22)
@HeadRowHeight(25)
public class AtmTaskCardDeliverHead {
	@ExcelProperty(value = { "行1标题", "序号" })
	@ColumnWidth(5)
	private Integer index;
	
	@ExcelProperty(value = { "行1标题", "吞没卡号" })
	@ColumnWidth(23)
    private String cardNo;

	@ExcelProperty(value = { "行1标题", "是否昨日吞卡" })
	@ColumnWidth(8)
    private String isYesterday;

	@ExcelProperty(value = { "行1标题", "设备编号" })
	@ColumnWidth(12)
	private String atmNo;

	@ExcelProperty(value = { "行1标题", "发卡银行" })
	@ColumnWidth(18)
    private String cardBankName;

	@ExcelProperty(value = { "行1标题", "带回线路" })
	@ColumnWidth(9)
    private String routeNo;

	@ExcelProperty(value = { "行1标题", "交送线路" })
	@ColumnWidth(9)
    private String deliveryRouteNo;

	@ExcelProperty(value = { "行1标题", "接收网点" })
	@ColumnWidth(18)
    private String deliverBankName;

	@ExcelProperty(value = { "行1标题", "接收日期" })
	@ColumnWidth(10)
    private String date;

	@ExcelProperty(value = { "行1标题", "制表人" })
	@ColumnWidth(10)
    private String makeMan;

	@ExcelProperty(value = { "行1标题", " 复核人" })
	@ColumnWidth(10)
    private String checkMan;

	@ExcelProperty(value = { "行1标题", "移交日期" })
	@ColumnWidth(10)
    private String deliverDate;

	@ExcelProperty(value = { "行1标题", "接收人" })
	@ColumnWidth(10)
    private String acceptManA;

	@ExcelProperty(value = { "行1标题", "接收人" })
	@ColumnWidth(10)
    private String acceptManB;
	
	public synchronized static void setExcelPropertyTitle(String topTitle) throws NoSuchFieldException, IllegalAccessException {
		Field[] fields = AtmTaskCardDeliverHead.class.getDeclaredFields();
		for (int j = 0; j < fields.length; j++) {
			Field field = fields[j];
			if (field.isAnnotationPresent(ExcelProperty.class)) {
				//获取SignalsRuleExportDTO字段上的ExcelProperty注解实例
				ExcelProperty excel = field.getAnnotation(ExcelProperty.class);
				//获取 ExcelProperty 这个代理实例所持有的 InvocationHandler
				InvocationHandler excelH = Proxy.getInvocationHandler(excel);
				Field excelF = excelH.getClass().getDeclaredField("memberValues");
				// 因为这个字段事 private final 修饰，所以要打开权限
				excelF.setAccessible(true);
				// 获取 memberValues
				Map excelValues = (Map) excelF.get(excelH);
				String[] oldValue = (String[]) excelValues.get("value");
				oldValue[0] = topTitle;
				// 修改 value 属性值
				excelValues.put("value", oldValue);
			}
		}
	}
}
