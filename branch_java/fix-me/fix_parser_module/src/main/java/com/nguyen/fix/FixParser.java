package com.nguyen.fix;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FixParser {
    private static final char DELIMITER = '\u0001';
    private static final FixTag[] requiredTags = {
            FixTag.BEGIN_STRING,
            FixTag.BODY_LENGTH,
            FixTag.MSG_TYPE,
            FixTag.SENDER_COMP_ID,
            FixTag.TARGET_COMP_ID,
            FixTag.CHECKSUM
    };

    public static Map<FixTag, String> parse(String message) throws InvalidFixFormatException {
        if (message == null || message.isEmpty()) {
            throw new InvalidFixFormatException("FIX message is empty");
        }
        Map<FixTag, String> fields = new LinkedHashMap<>();
        String[] pairs = message.split(String.valueOf(DELIMITER));

        for (String pair : pairs) {
            if (pair.isEmpty()) {
                continue;
            }

            int equalIndex = pair.indexOf('=');
            if (equalIndex <= 0) {
                throw new InvalidFixFormatException("Invalid pair format: " + pair);
            }

            String key = pair.substring(0, equalIndex);
            String value = pair.substring(equalIndex + 1);
            if (value.isEmpty()) {
                throw new InvalidFixFormatException("Invalid value for: " + key);
            }

            int tagInt;
            try {
                tagInt = Integer.parseInt(key);
            } catch (NumberFormatException e) {
                throw new InvalidFixFormatException("Invalid tag: " + key);
            }
            FixTag fixTag = FixTag.checkTag(tagInt);

            fields.put(fixTag, value);
        }
        validateRequired(fields);
        validateOrder(fields);
        validateBodyLength(message, fields);
        validateChecksum(message, fields);
        return fields;
    }

    private static void validateRequired(Map<FixTag, String> fields) {
        for (FixTag fixTag : requiredTags) {
            if (!fields.containsKey(fixTag)) {
                throw new InvalidFixFormatException("Missing required tag: " + fixTag);
            }
        }
    }

    private static void validateOrder(Map<FixTag, String> fields) {
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
        if (keys.getLast() != FixTag.CHECKSUM)
            throw new InvalidFixFormatException("Tag 10 (Checksum) must be last");
    }

    private static void validateChecksum(String message, Map<FixTag, String> fields) {
        try {
            int receivedChecksum = Integer.parseInt(fields.get(FixTag.CHECKSUM));
            int checksumIndex = message.lastIndexOf("10=");
            if (checksumIndex <= 0) {
                throw new InvalidFixFormatException("No Checksum field");
            }
            byte[] messageBytes = message.substring(0, checksumIndex).getBytes(StandardCharsets.US_ASCII);
            int calculatedChecksum = 0;
            for (byte b : messageBytes) {
                calculatedChecksum += b;
            }
            calculatedChecksum %= 256;
            if (calculatedChecksum != receivedChecksum) {
                throw new InvalidFixFormatException("Checksum incorrect: " + fields.get(FixTag.CHECKSUM));
            }
        } catch (NumberFormatException e) {
            throw new InvalidFixFormatException("Invalid checksum: " + fields.get(FixTag.CHECKSUM));
        }
    }

    private static void validateBodyLength(String message, Map<FixTag, String> fields) {
        try {
            int receivedBodyLength = Integer.parseInt(fields.get(FixTag.BODY_LENGTH));
            int messageTypeIndex = message.indexOf("35=");
            int checksumIndex = message.lastIndexOf("10=");
            if (messageTypeIndex <= 0 || checksumIndex <= 0) {
                throw new InvalidFixFormatException("Invalid FIX format");
            }
            int calculatedBodyLength = message.substring(messageTypeIndex, checksumIndex).length();
            if (receivedBodyLength != calculatedBodyLength) {
                throw new InvalidFixFormatException("Body Length incorrect: " + fields.get(FixTag.BODY_LENGTH));
            }
        } catch (NumberFormatException e) {
            throw new InvalidFixFormatException("Invalid Body Length: " + fields.get(FixTag.BODY_LENGTH));
        }
    }
}
