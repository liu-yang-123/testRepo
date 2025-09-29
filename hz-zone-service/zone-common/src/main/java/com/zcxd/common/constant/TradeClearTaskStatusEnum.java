package com.zcxd.common.constant;

/**
 * 清分任务状态
 * @ClassName TradeClearTaskStatusEnum
 * @Description TODO
 * @author 秦江南
 * @Date 2022年5月24日上午11:10:22
 */
public enum TradeClearTaskStatusEnum {
	CANCEL(-1,"已取消"),
    CREATE(0,"已创建"),
    CONFRIM(1,"已确认"),
    FINISH(2,"已完成");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    TradeClearTaskStatusEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
    	TradeClearTaskStatusEnum[] businessModeEnums = values();
        for (TradeClearTaskStatusEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
