package com.zcxd.gun.dto.excel;

import com.alibaba.excel.annotation.ExcelProperty;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @author zccc
 */
public abstract class ExportHead {
    public synchronized static <T> void setExcelPropertyTitle(Class<T> clazz, String topTitle) throws NoSuchFieldException, IllegalAccessException {
        Field[] fields = clazz.getDeclaredFields();
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
