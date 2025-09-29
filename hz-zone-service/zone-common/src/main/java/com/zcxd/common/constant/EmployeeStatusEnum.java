package com.zcxd.common.constant;

/**
 * 员工状态
 */
public enum EmployeeStatusEnum {
    WORK(0,"在职"),
    QUIT(1,"离职");
    private int value;

    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    EmployeeStatusEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
        EmployeeStatusEnum[] businessModeEnums = values();
        for (EmployeeStatusEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
