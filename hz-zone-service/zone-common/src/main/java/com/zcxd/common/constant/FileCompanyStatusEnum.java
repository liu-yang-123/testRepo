package com.zcxd.common.constant;

public enum FileCompanyStatusEnum {
	ENABLE(0,"启用"),
    DISABLE(1,"禁用");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    FileCompanyStatusEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
    	FileCompanyStatusEnum[] businessModeEnums = values();
        for (FileCompanyStatusEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
