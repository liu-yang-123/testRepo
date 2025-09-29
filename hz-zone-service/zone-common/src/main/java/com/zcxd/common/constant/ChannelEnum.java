package com.zcxd.common.constant;

/**
 * 数据删除状态
 */
public enum ChannelEnum {
    FROM_PC(0,"排班"),
    FROM_MOBILE(1,"临时");
    private int value;

    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    ChannelEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
        ChannelEnum[] channelEnums = values();
        for (ChannelEnum channelEnum : channelEnums) {
            if (channelEnum.getValue()==value) {
                return channelEnum.getText();
            }
        }
        return "";
    }
}
