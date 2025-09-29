package com.zcxd.common.constant;

/**
 * 清分类型
 * @ClassName TradeClearTypeEnum
 * @Description TODO
 * @author 秦江南
 * @Date 2022年5月25日上午11:01:02
 */
public enum TradeClearTypeEnum {
	RECEIVE(1,"领现"),
	RETURN(2,"回笼"),
	TAILBOX(3,"尾箱");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    TradeClearTypeEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
    	TradeClearTypeEnum[] businessModeEnums = values();
        for (TradeClearTypeEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
