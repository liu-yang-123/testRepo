package com.zcxd.common.constant;

public enum CollectSendTaskReportEnum {
	FIXED_ISSUE(1,"固定下发"),
	FIXED_PAY(2,"固定上缴"),
	TEMP_ISSUE(3,"临时下发"),
	TEMP_PAY(4,"临时上缴");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    CollectSendTaskReportEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
    	CollectSendTaskReportEnum[] businessModeEnums = values();
        for (CollectSendTaskReportEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
    
}
