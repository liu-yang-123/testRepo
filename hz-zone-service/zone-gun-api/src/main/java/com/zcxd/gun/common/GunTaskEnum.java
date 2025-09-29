package com.zcxd.gun.common;

/**
 * @author zccc
 */

public enum GunTaskEnum {
    START(1, "未发枪"),
    ISSUEDGUN(2, "已发枪"),
    FINISHED(3,"已完成"),
    CANCEL(4,"取消");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    GunTaskEnum(final int value, final String text) {
        this.value = value;
        this.text = text;
    }

    public static boolean containValue(String str) {
        return START.getText().equals(str) || ISSUEDGUN.getText().equals(str) || FINISHED.getText().equals(str) || CANCEL.getText().equals(str);
    }

    public static String getTextByValue(Integer value) {
        if (START.getValue() == value) {
            return START.getText();
        } else if (ISSUEDGUN.getValue() == value) {
            return ISSUEDGUN.getText();
        } else if (FINISHED.getValue() == value) {
            return FINISHED.getText();
        }else if (CANCEL.getValue() == value) {
            return CANCEL.getText();
        } else {
            return "";
        }
    }

    public static Integer getValuetByText(String text) {
        if (START.getText().equals(text)) {
            return START.getValue();
        } else if (ISSUEDGUN.getText().equals(text)) {
            return ISSUEDGUN.getValue();
        } else if (FINISHED.getText().equals(text)) {
            return FINISHED.getValue();
        }else if (CANCEL.getText().equals(text)) {
            return CANCEL.getValue();
        } else {
            return -1;
        }
    }
}
