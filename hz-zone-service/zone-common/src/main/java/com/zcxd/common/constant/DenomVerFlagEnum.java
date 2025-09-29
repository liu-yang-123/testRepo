package com.zcxd.common.constant;

public enum DenomVerFlagEnum {

    VER_NO(0,"无版别"), //不包括版别
    VER_YES(1,"带版别"), //包括版别
    VER_BAD(2,"残缺券"); //残缺币
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }

    DenomVerFlagEnum(int value,String text) {
        this.value = value;
        this.text = text;
    }

    public String getText() {
        return this.text;
    }
    public static String getText(int value) {
        DenomVerFlagEnum[] flagEnums = values();
        for (DenomVerFlagEnum flagEnum : flagEnums) {
            if (flagEnum.getValue()==value) {
                return flagEnum.getText();
            }
        }
        return "";
    }
}
