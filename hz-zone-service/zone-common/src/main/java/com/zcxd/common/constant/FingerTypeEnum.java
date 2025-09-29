package com.zcxd.common.constant;

public enum FingerTypeEnum {
	STAFF(0,"员工"),
	COUNTER(1,"柜员");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    FingerTypeEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
    	FingerTypeEnum[] businessModeEnums = values();
        for (FingerTypeEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
