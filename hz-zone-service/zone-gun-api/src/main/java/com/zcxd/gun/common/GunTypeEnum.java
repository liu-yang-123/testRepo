package com.zcxd.gun.common;

public enum GunTypeEnum {
    GUN(1,"枪支"),
    AMMUNITION(2,"弹盒");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    GunTypeEnum(final int value, final String text) {
        this.value = value;
        this.text = text;
    }

    public static boolean containValue(String str) {
        return GUN.getText().equals(str) || AMMUNITION.getText().equals(str);
    }

    public static String getTextByValue(Integer value) {
        if (GUN.getValue() == value) {
            return GUN.getText();
        } else if (AMMUNITION.getValue() == value) {
            return AMMUNITION.getText();
        } else {
            return "";
        }
    }
}
