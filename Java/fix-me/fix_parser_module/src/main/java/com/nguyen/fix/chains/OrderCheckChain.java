package com.nguyen.fix.chains;

import com.nguyen.fix.FixTag;
import com.nguyen.fix.InvalidFixFormatException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderCheckChain extends ChainAbstract {
    @Override
    public void handle(String rawFix, Map<FixTag, String> fields) throws InvalidFixFormatException {
        List<FixTag> keys = new ArrayList<>(fields.keySet());
        if (keys.getFirst() != FixTag.BEGIN_STRING) {
            throw new InvalidFixFormatException("Tag 8 (BeginString) must be first");
        }
        if (keys.get(1) != FixTag.BODY_LENGTH) {
            throw new InvalidFixFormatException("Tag 9 (BodyLength) must be second");
        }
        if (keys.get(2) != FixTag.MSG_TYPE) {
            throw new InvalidFixFormatException("Tag 35 (MessageType) must be third");
        }
        if (keys.get(3) != FixTag.SENDER_COMP_ID) {
            throw new InvalidFixFormatException("Tag 49 (SenderCompId) must be Fourth");
        }
        if (keys.getLast() != FixTag.CHECKSUM) {
            throw new InvalidFixFormatException("Tag 10 (Checksum) must be last");
        }

        if (next != null) {
            next.handle(rawFix, fields);
        }
    }
}
