package com.zcxd.base.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EnumUtil {

    /**
     * 判断数值是否属于枚举类的值
     * @param clzz 枚举类 Enum
     * @param value
     * @author wayleung
     * @return
     */
    public static boolean isInclude(Class<?> clzz,Integer value) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        boolean include = false;
        if(clzz.isEnum()){
            Object[] enumConstants = clzz.getEnumConstants();
            Method getValue = clzz.getMethod("getValue");
            for (Object enumConstant:enumConstants){
                if (getValue.invoke(enumConstant).equals(value)) {
                    include = true;
                    break;
                }
            }
        }
        return include;
    }

    /**
     * 判断数值是否属于枚举类的值
     * @param clzz 枚举类 Enum
     * @param value
     * @author wayleung
     * @return
     */
    public static boolean isInclude(Class<?> clzz,String value) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        boolean include = false;
        if(clzz.isEnum()){
            Object[] enumConstants = clzz.getEnumConstants();
            Method getValue = clzz.getMethod("getValue");
            for (Object enumConstant:enumConstants){
                if (getValue.invoke(enumConstant).equals(value)) {
                    include = true;
                    break;
                }
            }
        }
        return include;
    }
}
