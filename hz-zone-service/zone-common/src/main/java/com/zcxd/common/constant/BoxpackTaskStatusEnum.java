package com.zcxd.common.constant;

public enum BoxpackTaskStatusEnum {
	CHECKED(1,"已审核"),
    FINISH(2,"已完成");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    BoxpackTaskStatusEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
    	BoxpackTaskStatusEnum[] businessModeEnums = values();
        for (BoxpackTaskStatusEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
