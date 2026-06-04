package com.nguyen.fix;

import com.nguyen.colors.Colors;
import com.nguyen.fix.chains.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class FixParser {
    public static final char SOH = '\u0001';
    private static final Chain validationChain;
    static {
        ChainAbstract head = new MandatoryCheckChain();
        head.setNext(new OrderCheckChain())
                .setNext(new BodyLengthValidationChain())
                .setNext(new ChecksumValidationChain())
                .setNext(new FormatValidationChain());
        validationChain = head;
    }

    public static Map<FixTag, String> parse(String message) throws InvalidFixFormatException {
        if (message == null || message.isEmpty()) {
            throw new InvalidFixFormatException("FIX message is empty");
        }
        Map<FixTag, String> fields = new LinkedHashMap<>();
        String[] pairs = message.split(String.valueOf(SOH));

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

            if (fields.containsKey(fixTag)) {
                throw new InvalidFixFormatException("Duplicate tag: " + tagInt);
            }
            fields.put(fixTag, value);
        }

       validationChain.handle(message, fields);
        return fields;
    }

    public static String extractRawTargetId(String message) {
        String search = '\u0001' + "56=";
        int start = message.indexOf(search);
        if (start == -1) {
            System.err.println(Colors.RED + "Error: " + Colors.RESET + "Invalid Target Id, throw to the trash");
            return "000000";
        }
        start += search.length();
        int end = message.indexOf('\u0001', start);
        if (end == -1) {
            System.err.println(Colors.RED + "Error: " + Colors.RESET + "Invalid Target Id, throw to the trash");
            return "000000";
        }
        return message.substring(start, end);
    }
}
