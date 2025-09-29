package com.zcxd.common.constant;

/**
 * 休息计划调整类别
 */
public enum VacateAdjustTypeEnum {
    VACATE(0,"休假"),
    WORK(1,"加班"),
    REPLACE(2,"替班");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    VacateAdjustTypeEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
        VacateAdjustTypeEnum[] businessModeEnums = values();
        for (VacateAdjustTypeEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
