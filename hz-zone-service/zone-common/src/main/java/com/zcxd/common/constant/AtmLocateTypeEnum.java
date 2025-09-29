package com.zcxd.common.constant;

/**
 * atm位置类型
 */
public enum AtmLocateTypeEnum {
    INDEPENT(1,"离行式"),
    ATTACH(2,"附行式"),
    INSIDE(3,"大堂式");
    private int value;
    private String text;


    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    AtmLocateTypeEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
        AtmLocateTypeEnum[] businessModeEnums = values();
        for (AtmLocateTypeEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
