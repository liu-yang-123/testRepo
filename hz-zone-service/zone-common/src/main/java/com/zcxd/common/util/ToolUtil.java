package com.zcxd.common.util;


import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**接口请求返回数据对象
 * @author songanwei
 * @date 2021/4/15 16:15
 */

public class ToolUtil{

    public static boolean isArrayEmpty(Object[] array) {
        return null == array || 0 == array.length;
    }

    public static String longArrayToString(List<Long> list) {
        return list.toString().replace("[","").replace("]","").replace(" ","");
    }

    public static Long[] stringToLongArray(String str) {
        if (!StringUtils.isEmpty(str)) {
            String[] strArray = str.split(",");
            Long[] longArray = new Long[strArray.length];
            for (int i = 0; i < strArray.length; i++) {
                longArray[i] = Long.parseLong(strArray[i]);
            }
            return longArray;
        }
        return null;
    }

    public static String intListToString(List<Integer> list) {
        return list.toString().replace("[","").replace("]","").replace(" ","");
    }

    public static List<Integer> stringToIntList(String str) {
        if (!StringUtils.isEmpty(str)) {
            String[] strArray = str.split(",");
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < strArray.length; i++) {
                list.add(Integer.parseInt(strArray[i]));
            }
            return list;
        }
        return null;
    }

    /**
     * 返回字符串最后一位数字字符串  如无数字返回null
     * @param number 字符串
     * @return
     */
    public static String getLastNumber(String number){
        char num[] = number.toCharArray();
        String result = null;
        for (int k = num.length-1; k > 0; k--){
            if (Character.isDigit(num[k])){
                result = String.valueOf(num[k]);
                break;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        String[] testArray = new String[0];
        System.out.println("1: " + isArrayEmpty(testArray));
        System.out.println("2 = " + isArrayEmpty(testArray));
        List<Long> list = new ArrayList<>();
        list.add(1L);
        list.add(2L);
        list.add(3L);
        System.out.println("3 = " + list.toString());
    }
}
