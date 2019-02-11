package com.cts.batch.enums;

public enum ApplicationConstants {
    ACT_NBR_START_POSITION(1),
    ACT_NBR_END_POSITION(17),
    BANK_ACT_NBR_START_POSITION(18),
    BANK_ACT_NBR_END_POSITION(35),
    CARD_MEMBER_NAME_START_POSITION(36),
    CARD_MEMBER_NAME_END_POSITION(65),
    PAYMENT_DUE_DATE_START_POSITION(66),
    PAYMENT_DUE_DATE_END_POSITION(73);
    private int value;

    ApplicationConstants(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
