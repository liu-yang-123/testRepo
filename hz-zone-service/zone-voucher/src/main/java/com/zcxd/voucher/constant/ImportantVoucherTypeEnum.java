package com.zcxd.voucher.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @author zccc
 */
public enum ImportantVoucherTypeEnum {
    CARD(1,"卡类"),
    NOBEL_METAL(2,"贵金属"),
    OTHER(3, "其他凭证类");
    @EnumValue
    private final int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    ImportantVoucherTypeEnum(final int value, final String text) {
        this.value = value;
        this.text = text;
    }

    public static boolean containValue(String str) {
        return CARD.getText().equals(str) || NOBEL_METAL.getText().equals(str) || OTHER.getText().equals(str);
    }

    public static Integer getValueByText(String str) {
        if (CARD.getText().equals(str)) {
            return CARD.getValue();
        } else if (NOBEL_METAL.getText().equals(str)) {
            return NOBEL_METAL.getValue();
        } else if (OTHER.getText().equals(str)) {
            return OTHER.getValue();
        } else {
            return -1;
        }
    }

    public static ImportantVoucherTypeEnum getByStr(String str) {
        if (CARD.getText().equals(str)) {
            return CARD;
        } else if (NOBEL_METAL.getText().equals(str)) {
            return NOBEL_METAL;
        } else {
            return OTHER;
        }
    }

    public static String getTextByValue(Integer value) {
        if (CARD.getValue() == value) {
            return CARD.getText();
        } else if (NOBEL_METAL.getValue() == value) {
            return NOBEL_METAL.getText();
        } else if (OTHER.getValue() == value) {
            return OTHER.getText();
        } else {
            return "";
        }
    }
}
