package com.zcxd.gun.common;

public enum GunStatusEnum {
    STOREIN(1,"库存"),
    STOREOUT(2,"已发出"),
    UNABLE(3,"禁用");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    GunStatusEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }
}
