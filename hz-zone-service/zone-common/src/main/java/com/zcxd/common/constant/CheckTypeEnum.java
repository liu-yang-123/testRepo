package com.zcxd.common.constant;

public enum CheckTypeEnum {

        APPROVE(1),//同意
        REJECT(0); //拒绝

        private int value;

        public int getValue() {
            return this.value;
        }

        CheckTypeEnum(int value) {
            this.value = value;
        }
}
