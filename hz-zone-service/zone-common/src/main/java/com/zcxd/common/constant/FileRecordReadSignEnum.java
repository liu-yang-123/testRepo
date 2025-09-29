package com.zcxd.common.constant;

public enum FileRecordReadSignEnum {
	UNREAD(0,"未读"),
    READ(1,"已读");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    FileRecordReadSignEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
    	FileRecordReadSignEnum[] businessModeEnums = values();
        for (FileRecordReadSignEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
