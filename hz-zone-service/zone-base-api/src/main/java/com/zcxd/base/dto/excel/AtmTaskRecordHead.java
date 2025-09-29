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

/**
 * 
 * @ClassName AtmTaskRecordHead
 * @Description TODO
 * @author 秦江南
 * @Date 2021年9月3日下午3:38:49
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(25)
public class AtmTaskRecordHead {
	@ExcelProperty(value = { "行1标题", "序号" })
	@ColumnWidth(5)
	private Integer index;
	
	@ExcelProperty(value = { "行1标题", "线路" })
	@ColumnWidth(9)
	private String routeNo;
	
	@ExcelProperty(value = { "行1标题", "网点名" })
	@ColumnWidth(30)
	private String bankName;
	
	@ExcelProperty(value = { "行1标题", "设备编号" })
	@ColumnWidth(9)
	private String terNo;
	
	@ExcelProperty(value = { "行1标题", "任务事项" })
	@ColumnWidth(20)
	private String repairContent;
	
	@ExcelProperty(value = { "行1标题", "备注" })
	@ColumnWidth(20)
	private String comments;
	

	@ExcelProperty(value = { "行1标题", "记录时间" })
	@ColumnWidth(15)
	private String endTime;

	@ExcelProperty(value = { "行1标题", "情况查询时间" })
	@ColumnWidth(15)
	private String queryTime;

	@ExcelProperty(value = { "行1标题", "情况说明时间" })
	@ColumnWidth(15)
	private String explainTime;


	public synchronized static void setExcelPropertyTitle(String topTitle) throws NoSuchFieldException, IllegalAccessException {
		Field[] fields = AtmTaskRecordHead.class.getDeclaredFields();
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
