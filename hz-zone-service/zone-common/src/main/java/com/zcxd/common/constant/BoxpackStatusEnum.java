package com.zcxd.common.constant;

public enum BoxpackStatusEnum {
	UNKNOWN(0,"未知"),
	BANK(1,"网点"),
	ROUTE(2,"途中"),
	STORAGE(3,"库房");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    BoxpackStatusEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
    	BoxpackStatusEnum[] businessModeEnums = values();
        for (BoxpackStatusEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
