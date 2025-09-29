package com.zcxd.common.constant;

/**
 * 钞盒状态
 */
public enum BoxStatusEnum {
    PACK(0,"包装"),
    STOREIN(1,"入库"),
    STOREOUT(2,"出库"),
    OPEN(3,"拆封");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    BoxStatusEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
        BoxStatusEnum[] businessModeEnums = values();
        for (BoxStatusEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
