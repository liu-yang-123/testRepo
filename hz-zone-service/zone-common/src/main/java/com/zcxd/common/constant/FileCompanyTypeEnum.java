package com.zcxd.common.constant;

public enum FileCompanyTypeEnum {
	COMPANY(0,"公司"),
    BANK(1,"银行");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    FileCompanyTypeEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
    	FileCompanyTypeEnum[] businessModeEnums = values();
        for (FileCompanyTypeEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
