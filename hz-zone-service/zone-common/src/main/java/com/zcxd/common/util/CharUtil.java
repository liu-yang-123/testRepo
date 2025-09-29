package com.zcxd.common.util;

import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharUtil {

    public static String getRandomString(Integer num) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < num; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static String getRandomNum(Integer num) {
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < num; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 从身份证获取出生日期
     * @param idNo - 身份证字符串
     * @return yyyy-mm-dd
     */
    public static String getBirthday(String idNo) {
        // 出生日期
        String birthday = "";
        // 身份证号不为空
        if (idNo.length() == 15) {
            birthday = "19" + idNo.substring(6, 8) + "-" + idNo.substring(8, 10) + "-" + idNo.substring(10, 12);
        } else if (idNo.length() == 18) {
            birthday = idNo.substring(6, 10) + "-" + idNo.substring(10, 12) + "-" + idNo.substring(12, 14);
        }
        return birthday;
    }

    public static String filterPathValue(String url) {
        System.out.println("++++filterPathValue: " + url);
        String[] paths = url.split("/");
        String newUrl = "";
        Pattern pattern = Pattern.compile("[0-9]*");
        System.out.println("paths len = " + paths.length);
        for (int i=0; i<paths.length;i++) {
            System.out.println("paths[] " + i + " = " + paths[i]);
            if (!StringUtils.isEmpty(paths[i])) {
                Matcher isNum = pattern.matcher(paths[i]);
                if (isNum.matches()) {
                    break;
                } else {
                    newUrl += "/" + paths[i];
                }
            }
        }
        System.out.println("------filterPathValue: " + newUrl);
        return newUrl;
    }

    /**
     * 判断是否为纯数字字符串
     * @param str
     * @return
     */
    public static boolean isNumberic(String str) {
        if(StringUtils.isEmpty(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * 判断是否为数字字符串，包括小数点，-号
     * @param str
     * @return
     */
    public static boolean isDigital(String str) {
        if(StringUtils.isEmpty(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    public static String getUUID() {
        String uuid = UUID.randomUUID().toString(); //转化为String对象
        System.out.println(uuid);//打印UUID
        uuid = uuid.replace("-", "");//因为UUID本身为32位只是生成时多了“-”，所以将它们去点就可
        return uuid;
    }

    public static void main(String[] args) {

        String str="12399834";
        String str2="-12399.834";
        String str3="12399.834";
        System.out.println(str + "test =" + isDigital(str));
        System.out.println(str + "test =" + isDigital(str2));
        System.out.println(str + "test =" + isDigital(str3));
        System.out.println("uuid = " + getUUID());
    }
}
