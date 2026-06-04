package com.nguyen.fix.chains;

import com.nguyen.fix.FixTag;
import com.nguyen.fix.InvalidFixFormatException;

import java.util.Map;

public class MandatoryCheckChain extends ChainAbstract {
    private static final FixTag[] requiredMainTags = {
            FixTag.BEGIN_STRING,
            FixTag.BODY_LENGTH,
            FixTag.MSG_TYPE,
            FixTag.SENDER_COMP_ID,
            FixTag.SENDING_TIME,
            FixTag.TARGET_COMP_ID,
            FixTag.CHECKSUM
    };
    private static final FixTag[] requiredOrderTags = {
            FixTag.ORDER_ID,
            FixTag.SIDE,
            FixTag.SYMBOL,
            FixTag.ORDER_QTY,
            FixTag.PRICE,
    };
    private static final FixTag[] requiredStatusTags = {
            FixTag.ORDER_ID,
            FixTag.ORD_STATUS,
    };

    @Override
    public void handle(String rawFix, Map<FixTag, String> fields) throws InvalidFixFormatException {
        for (FixTag fixTag : requiredMainTags) {
            if (!fields.containsKey(fixTag)) {
                throw new InvalidFixFormatException("Missing required tag: " + fixTag);
            }
        }
        String msgType = fields.get(FixTag.MSG_TYPE);
        if (msgType.equals("D")) {
            for (FixTag fixTag : requiredOrderTags) {
                if (!fields.containsKey(fixTag)) {
                    throw new InvalidFixFormatException("Missing required tag: " + fixTag);
                }
            }
        } else if (msgType.equals("8")) {
            for (FixTag fixTag : requiredStatusTags) {
                if (!fields.containsKey(fixTag)) {
                    throw new InvalidFixFormatException("Missing required tag: " + fixTag);
                }
            }
        } else if (!msgType.equals("3") && !msgType.equals("A")) {
            throw new InvalidFixFormatException("Unknown MsgType: " + msgType);
        }

        if (next != null) {
            next.handle(rawFix, fields);
        }
    }
}
