package com.zcxd.common.constant;

public enum BankTypeEnum {
	ATM(0,"清机加钞"),
    BOX(1,"早送晚收"),
	CLEAR(2,"商业清分");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    BankTypeEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
    	BankTypeEnum[] businessModeEnums = values();
        for (BankTypeEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
