package com.zcxd.common.constant;

/**
 * 司机班次类型
 */
public enum DriverTypeEnum {
    MAIN(0,"主班"),
    BACKUP(1,"替班");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    DriverTypeEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
        DriverTypeEnum[] businessModeEnums = values();
        for (DriverTypeEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
