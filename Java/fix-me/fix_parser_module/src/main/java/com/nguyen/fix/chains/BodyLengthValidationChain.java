package com.nguyen.fix.chains;

import com.nguyen.fix.FixTag;
import com.nguyen.fix.InvalidFixFormatException;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class BodyLengthValidationChain extends ChainAbstract {
    @Override
    public void handle(String rawFix, Map<FixTag, String> fields) throws InvalidFixFormatException {
        try {
            int receivedBodyLength = Integer.parseInt(fields.get(FixTag.BODY_LENGTH));
            int messageTypeIndex = rawFix.indexOf("35=");
            int checksumIndex = rawFix.lastIndexOf("10=");
            if (messageTypeIndex <= 0 || checksumIndex <= 0) {
                throw new InvalidFixFormatException("Invalid FIX format");
            }
            int calculatedBodyLength = rawFix.substring(messageTypeIndex, checksumIndex).getBytes(StandardCharsets.US_ASCII).length;
            if (receivedBodyLength != calculatedBodyLength) {
                throw new InvalidFixFormatException("Body Length incorrect: " + fields.get(FixTag.BODY_LENGTH));
            }
        } catch (NumberFormatException e) {
            throw new InvalidFixFormatException("Invalid Body Length: " + fields.get(FixTag.BODY_LENGTH));
        }

        if (next != null) {
            next.handle(rawFix, fields);
        }
    }
}
