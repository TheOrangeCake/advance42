package com.nguyen.fix;

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

    public static Map<FixTag, String> parse(String message) {
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

    }

    private static void validateBodyLength(String message, Map<FixTag, String> fields) {

    }
}
