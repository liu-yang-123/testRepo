package com.zcxd.base.util;

public class DenomUtil {
    public static String denomValueToName(String valueStr, boolean isCoin) {
        String name = "";
        String prefix = "";
        if(isCoin) {
            prefix = "硬币";
        }
        if (valueStr.equals("100.00")) {
            name = "100元";
        } else if (valueStr.equals("50.00")) {
            name = "50元";
        } else if (valueStr.equals("20.00")) {
            name = "20元";
        } else if (valueStr.equals("10.00")) {
            name = "10元";
        } else if (valueStr.equals("5.00")) {
            name = "5元";
        } else if (valueStr.equals("2.00")) {
            name = "2元";
        } else if (valueStr.equals("1.00")) {
            name = "1元"+prefix;
        } else if (valueStr.equals("0.50")) {
            name = "5角"+prefix;
        } else if (valueStr.equals("0.20")) {
            name = "2角"+prefix;
        } else if (valueStr.equals("0.10")) {
            name = "1角"+prefix;
        } else if (valueStr.equals("0.05")) {
            name = "5分"+prefix;
        } else if (valueStr.equals("0.02")) {
            name = "2分"+prefix;
        } else if (valueStr.equals("0.01")) {
            name = "1分"+prefix;
        } else if(valueStr.equals("0")) {
            name = "通用";
        }
        return name;
    }
}
