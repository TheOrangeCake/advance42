package com.nguyen.fix;

// FIX 4.4: https://www.onixs.biz/fix-dictionary/4.4/fields_by_tag.html
public enum FixTag {
    BEGIN_STRING(8),
    BODY_LENGTH(9),
    MSG_TYPE(35), // Error = 3 -> fill 58, Buy/Sell = D -> fill 54, Status = 8 -> fill 39, Logon = A
    SENDER_COMP_ID(49),
    TARGET_COMP_ID(56),
    ORDER_ID(11),
    SENDING_TIME(52),
    SYMBOL(55), // Instrument
    ORDER_QTY(38),
    PRICE(44),
    SIDE(54), // Buy = 1 or Sell = 2
    ORD_STATUS(39), // Executed = 2 or Rejected = 8
    TEXT(58),
    CHECKSUM(10);

    private final int tag;

    FixTag(int tag) {
        this.tag = tag;
    }

    public int getTag() {
        return this.tag;
    }

    public static FixTag checkTag(int tag) {
        for (FixTag fixTag : values()) {
            if (fixTag.getTag() == tag) {
                return fixTag;
            }
        }
        throw new InvalidFixFormatException("Unknown FIX Tag: " + tag);
    }
}
