package com.zcxd.base.dto.excel;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.*;

import lombok.Data;
import org.apache.poi.ss.usermodel.BorderStyle;

/**
 * 
 * @ClassName AtmTaskCardHead
 * @Description TODO
 * @author 秦江南
 * @Date 2021年9月3日下午3:38:38
 */
@Data
@ContentRowHeight(22)
@HeadRowHeight(25)
public class AtmTaskCardHead {
	@ExcelProperty(value = { "日期X", "序号" })
	@ColumnWidth(10)
	private Integer index;

//	@ExcelProperty(value = { "", "代码" })
//	@ColumnWidth(10)
//	private Integer code;

	@ExcelProperty(value = { "离附行式自助柜员机吞没卡交接单", "吞没卡号" })
	@ColumnWidth(23)
	private String cardNo;

	@ExcelProperty(value = { "离附行式自助柜员机吞没卡交接单", "设备编号" })
	@ColumnWidth(12)
	private String terNo;

	@ExcelProperty(value = { "离附行式自助柜员机吞没卡交接单", "移交人" })
	@ColumnWidth(9)
	private String transferor;

	@ExcelProperty(value = { "离附行式自助柜员机吞没卡交接单", "移交人" })
	@ColumnWidth(9)
	private String transferor2;

	@ExcelProperty(value = { "离附行式自助柜员机吞没卡交接单", "签收人" })
	@ColumnWidth(9)
	private String recipient;

	@ExcelProperty(value = { "线路", "签收人" })
	@ColumnWidth(9)
	private String recipient2;

	@ExcelProperty(value = { "X号线", "接收网点" })
	@ColumnWidth(18)
	private String deliverBankName;

	public synchronized static void setExcelPropertyTitle(String topTitle, String routeNo) throws NoSuchFieldException, IllegalAccessException {
		Field[] fields = AtmTaskCardHead.class.getDeclaredFields();
	    //字段特定处理
		for (int j = 0; j < fields.length; j++) {
			Field field = fields[j];
			//序号字段处理
			if ("index".equals(field.getName())){
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
            // 接收网点字段处理
			if ("deliverBankName".equals(field.getName())){
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
					oldValue[0] = routeNo + "号线";
					// 修改 value 属性值
					excelValues.put("value", oldValue);
				}
			}
		}
	}
}
