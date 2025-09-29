package com.zcxd.common.constant;

public enum BankCategoryTypeEnum {
	BUSINESS(1,"营业机构"),
    NOBUSINESS(2,"非营业机构"),
	STORAGE(3,"库房");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    BankCategoryTypeEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
    	BankCategoryTypeEnum[] businessModeEnums = values();
        for (BankCategoryTypeEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
